# ── builder stage ─────────────────────────────────────────────────────
FROM node:16 AS builder
WORKDIR /app

# 1) Copy only manifests, install deps (incl. our new plugin)
COPY package.json package-lock.json ./
RUN npm ci

# 2) Copy rest of your app and build it
COPY vite.config.js index.html ./
COPY public ./public
COPY src    ./src
RUN npm run build

# ── final stage ───────────────────────────────────────────────────────
FROM nginx:alpine
COPY default.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
