# แก้ไขปัญหา 502 Bad Gateway ใน Chrome

## สาเหตุที่เป็นไปได้

1. **Browser Cache/Cookies** - Chrome อาจมี cache เก่าหรือ cookies ที่ทำให้เกิดปัญหา
2. **Chrome Extensions** - Extensions บางตัวอาจบล็อก requests
3. **Chrome Security Settings** - Chrome มี security settings ที่เข้มงวดกว่า Edge
4. **CORS Preflight** - Chrome อาจ handle CORS preflight requests ต่างจาก Edge
5. **Service Worker** - ถ้ามี service worker อาจทำให้เกิดปัญหา

## วิธีแก้ไข

### 1. Clear Browser Cache และ Cookies

**วิธีที่ 1: ผ่าน Developer Tools**
1. เปิด Developer Tools (F12)
2. ไปที่แท็บ Network
3. ติ๊ก "Disable cache"
4. Refresh หน้าเว็บ (Ctrl+Shift+R)

**วิธีที่ 2: ผ่าน Settings**
1. ไปที่ `chrome://settings/clearBrowserData`
2. เลือก "Cached images and files" และ "Cookies and other site data"
3. เลือก "All time"
4. คลิก "Clear data"

### 2. ลอง Incognito Mode

1. เปิดหน้าต่าง Incognito (Ctrl+Shift+N)
2. ลองเข้าเว็บอีกครั้ง
3. ถ้าทำงานได้ แสดงว่าเป็นปัญหาจาก extensions หรือ cache

### 3. ตรวจสอบ Chrome Extensions

1. ไปที่ `chrome://extensions/`
2. ปิด extensions ที่อาจบล็อก requests เช่น:
   - Ad blockers (uBlock Origin, AdBlock Plus)
   - Privacy extensions (Privacy Badger, Ghostery)
   - Security extensions
3. Refresh หน้าเว็บ

### 4. ตรวจสอบ Network Tab

1. เปิด Developer Tools (F12)
2. ไปที่แท็บ Network
3. Refresh หน้าเว็บ
4. ดูว่ามี request ไหนที่ขึ้น 502
5. คลิกที่ request นั้นเพื่อดู details

### 5. ตรวจสอบ Server Logs

**ตรวจสอบ Nginx Logs:**
```bash
docker logs int221-frontend
```

**ตรวจสอบ Backend Logs:**
```bash
docker logs int221-backend
```

### 6. Restart Docker Containers

```bash
cd /path/to/project
docker-compose restart
```

### 7. ตรวจสอบ Backend Status

```bash
# ตรวจสอบว่า backend container ทำงานอยู่หรือไม่
docker ps | grep int221-backend

# ตรวจสอบ backend logs
docker logs int221-backend --tail 50
```

### 8. ตรวจสอบ Network Connection

```bash
# ตรวจสอบว่า backend ตอบสนองหรือไม่
curl http://localhost:8080/itb-mshop/v2/sale-items
```

## การแก้ไขที่ Server Side

### เพิ่ม Error Handling ใน Nginx

ถ้ายังมีปัญหา อาจต้องเพิ่ม error handling ใน nginx.conf:

```nginx
# เพิ่มใน location /or4/itb-mshop/
proxy_connect_timeout 60s;
proxy_send_timeout 60s;
proxy_read_timeout 60s;
proxy_buffering off;
```

### ตรวจสอบ CORS Headers

Chrome อาจต้องการ CORS headers ที่ชัดเจนกว่า:

```nginx
add_header 'Access-Control-Allow-Credentials' 'true' always;
add_header 'Access-Control-Expose-Headers' 'Content-Length,Content-Range' always;
```

## ถ้ายังแก้ไม่ได้

1. ลองใช้ Chrome Canary หรือ Chrome Beta
2. ตรวจสอบ Chrome version (อาจต้อง update)
3. ตรวจสอบ firewall หรือ antivirus ที่อาจบล็อก requests
4. ลองใช้ Chrome ใน Safe Mode (ปิด extensions ทั้งหมด)

