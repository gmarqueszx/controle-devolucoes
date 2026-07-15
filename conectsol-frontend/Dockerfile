FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM caddy:2
COPY --from=build /app/dist/conectsol-frontend/browser /srv
COPY Caddyfile /etc/caddy/Caddyfile
