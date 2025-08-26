# 🚀 Deployment Guide for Server

## 📋 Prerequisites
- Docker & Docker Compose installed
- Port 80, 8080, 3306 available
- Domain: ip24or4.sit.kmutt.ac.th configured

## 🔧 Configuration Changes Made

### 1. Database Configuration
- Backend connects to MySQL container: `mysql:3306`
- Database: `pbi1`
- User: `user`, Password: `mysql`

### 2. File Upload Directory
- Backend: `/app/uploads`
- Frontend: `/usr/share/nginx/html/uploads`
- Shared volume: `./uploads`

### 3. API URLs
- Frontend API: `http://ip24or4.sit.kmutt.ac.th`
- Backend API: `http://ip24or4.sit.kmutt.ac.th/itb-mshop/`

## 🐳 Deployment Steps

### 1. Clone and Setup
```bash
# Clone project to server
git clone <repository-url>
cd Integrated_OR4

# Create uploads directory with proper permissions
mkdir -p uploads
chmod 755 uploads
chown 1000:1000 uploads  # หรือ user ที่เหมาะสม
```

### 2. Build and Run
```bash
# Build all services
docker-compose build

# Run in background
docker-compose up -d

# Check status
docker-compose ps
```

### 3. Verify Deployment
```bash
# Check logs
docker-compose logs -f

# Test endpoints
curl http://ip24or4.sit.kmutt.ac.th
curl http://ip24or4.sit.kmutt.ac.th/itb-mshop/v1/sale-items
```

## 🔍 Troubleshooting

### Database Issues
```bash
# Check MySQL container
docker-compose logs mysql

# Access MySQL
docker-compose exec mysql mysql -u user -p pbi1
```

### Backend Issues
```bash
# Check backend logs
docker-compose logs backend

# Restart backend
docker-compose restart backend
```

### Frontend Issues
```bash
# Check frontend logs
docker-compose logs frontend

# Check nginx config
docker-compose exec frontend nginx -t
```

### File Upload Issues
```bash
# Check uploads directory permissions
ls -la uploads/

# Fix permissions if needed
chmod -R 755 uploads/
chown -R 1000:1000 uploads/  # หรือ user ที่เหมาะสม

# Check if backend can write to uploads
docker-compose exec backend ls -la /app/uploads
```

## 📊 Monitoring

### Health Checks
- Frontend: `http://ip24or4.sit.kmutt.ac.th`
- Backend: `http://ip24or4.sit.kmutt.ac.th/itb-mshop/v1/sale-items`
- Database: `docker-compose exec mysql mysqladmin ping`

### Logs
```bash
# All services
docker-compose logs

# Specific service
docker-compose logs [service-name]

# Follow logs
docker-compose logs -f [service-name]
```

## 🔄 Maintenance

### Update Application
```bash
# Pull latest changes
git pull

# Rebuild and restart
docker-compose down
docker-compose build
docker-compose up -d
```

### Backup Database
```bash
# Create backup
docker-compose exec mysql mysqldump -u user -p pbi1 > backup.sql

# Restore backup
docker-compose exec -T mysql mysql -u user -p pbi1 < backup.sql
```

### Clean Up
```bash
# Remove unused images
docker image prune

# Remove unused volumes
docker volume prune

# Full cleanup
docker system prune -a
```

## 🌐 Access URLs
- **Frontend**: http://ip24or4.sit.kmutt.ac.th
- **Backend API**: http://ip24or4.sit.kmutt.ac.th/itb-mshop/
- **Uploaded Files**: http://ip24or4.sit.kmutt.ac.th/uploads/

## 📞 Support
If issues persist, check:
1. Docker and Docker Compose versions
2. Port availability
3. Domain DNS configuration
4. Server firewall settings
