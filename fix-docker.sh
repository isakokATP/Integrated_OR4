#!/bin/bash

echo "🛑 Stopping all containers..."
docker-compose down

echo "🗑️ Removing MySQL volume..."
docker volume rm integrated_or4_mysql-data 2>/dev/null || echo "Volume not found, continuing..."

echo "🧹 Cleaning up unused volumes..."
docker volume prune -f

echo "🚀 Starting MySQL first..."
docker-compose up -d mysql

echo "⏳ Waiting for MySQL to be ready..."
sleep 30

echo "🚀 Starting Backend..."
docker-compose up -d backend

echo "⏳ Waiting for Backend to be ready..."
sleep 10

echo "🚀 Starting Frontend..."
docker-compose up -d frontend

echo "📊 Checking container status..."
docker ps

echo "✅ Done! Check logs with: docker logs int221-backend"
