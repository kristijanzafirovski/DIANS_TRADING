FROM node:16 AS builder
WORKDIR /app
COPY package.json ./
RUN npm install
COPY public public
COPY src src
RUN npm run build
FROM nginx:alpine
COPY default.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/build /usr/share/nginx/html
EXPOSE 80
