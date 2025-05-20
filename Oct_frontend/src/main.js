import "./assets/main.css";
import "./assets/iphone.png";

import { createApp } from "vue";
import App from "./App.vue";
import router from "./routes";
import { vTrim } from "./directives/trim";

const app = createApp(App);

app.directive("trim", vTrim);
app.use(router);
app.mount("#app");
