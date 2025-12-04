#!/bin/sh
set -e

# Default backend URL if not set
BACKEND_URL=${BACKEND_URL:-http://localhost:8080}

echo "Configuring nginx with BACKEND_URL: $BACKEND_URL"

# Substitute environment variables in nginx config
envsubst '${BACKEND_URL}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf

# Test nginx configuration
nginx -t

# Start nginx
exec nginx -g 'daemon off;'
