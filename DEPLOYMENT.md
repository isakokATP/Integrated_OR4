# Deployment Guide for University Server

## Overview
This project is deployed on the university server at `http://intproj24.sit.kmutt.ac.th/or4` using Docker containers.

## Prerequisites
- VPN connection to KMUTT network
- Access to the university server
- Docker and Docker Compose installed on the server

## Deployment Steps

### 1. Local Development
```bash
# Clone the repository
git clone <your-repo-url>
cd INT222

# Start local development
cd Oct_frontend
npm install
npm run dev

# Backend will run on port 8080
# Frontend will run on port 5173
```

### 2. Push to Git
```bash
git add .
git commit -m "Update for production deployment"
git push origin main
```

### 3. Server Deployment
```bash
# SSH to university server
ssh user@ip24or4.sit.kmutt.ac.th

# Navigate to project directory
cd /path/to/project

# Pull latest changes
git pull origin main

# Build and start containers
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

### 4. Access the Application
- **Local Development**: `http://localhost:5173`
- **Production**: `http://intproj24.sit.kmutt.ac.th/or4` (requires VPN)

## Configuration Files

### Nginx Configuration
- **File**: `Oct_frontend/nginx.conf`
- **Purpose**: Routes `/or4` requests to frontend and backend
- **Key Features**:
  - Handles `/or4` path prefix
  - Proxies API requests to backend
  - Serves static files with proper caching

### Docker Compose
- **File**: `docker-compose.yml`
- **Services**:
  - `mysql`: Database (port 3306)
  - `backend`: Spring Boot API (port 8080)
  - `frontend`: Vue.js + Nginx (port 80)

### Environment Variables
- **Development**: Uses `VITE_API_URL_DEV`
- **Production**: Uses relative path `/or4`

## API Endpoints

### Frontend Routes
- **Base URL**: `/or4`
- **Home**: `/or4/`
- **Sale Items**: `/or4/sale-items`
- **Brands**: `/or4/brands`

### Backend API
- **Base URL**: `/or4/itb-mshop`
- **Sale Items**: `/or4/itb-mshop/v1/sale-items`
- **Brands**: `/or4/itb-mshop/v1/brands`

## Troubleshooting

### Common Issues
1. **VPN Required**: Must connect to KMUTT VPN to access the server
2. **Port Conflicts**: Ensure ports 80, 8080, and 3306 are available
3. **Container Issues**: Check logs with `docker-compose logs`

### Useful Commands
```bash
# Check container status
docker-compose ps

# View logs
docker-compose logs frontend
docker-compose logs backend
docker-compose logs mysql

# Restart services
docker-compose restart frontend
docker-compose restart backend

# Rebuild and restart
docker-compose down
docker-compose up --build -d
```

## Security Notes
- Backend API is accessible through nginx proxy
- File uploads are limited to 10MB
- Static assets are cached for performance
- All requests go through `/or4` path prefix
