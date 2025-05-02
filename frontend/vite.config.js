import { fileURLToPath, URL } from "node:url";
import { defineConfig, loadEnv } from "vite";
import vue from "@vitejs/plugin-vue";

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd());

  const isProduction = env.IS_PRODUCTION === 'true';

  console.log('IS_PRODUCTION:', isProduction);

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    base: './',
    build: {
      assetsDir: 'assets',
      outDir: 'dist',
      rollupOptions: {
        output: {
          assetFileNames: 'assets/[name][extname]',
          chunkFileNames: 'assets/[name].js',
          entryFileNames: 'assets/[name].js',
        }
      }
    },
    server: {
      host: '0.0.0.0',
      port: 5173,
      watch: {
        usePolling: true,
      },
      proxy: {
        '/api': {
          target: isProduction ? env.VITE_API_URL_PROD : env.VITE_API_URL_DEV,
          changeOrigin: true,
          rewrite: path => path.replace(/^\/api/, ''),
        },
      },
    },
  }
})