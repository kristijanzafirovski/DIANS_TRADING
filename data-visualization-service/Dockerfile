FROM node:16 AS builder
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY static/ ./static
COPY vite.config.js ./
RUN npm run build

FROM nginx:alpine
COPY default.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
