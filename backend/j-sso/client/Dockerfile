# syntax=docker/dockerfile:1

ARG NODE_VERSION=26.13.1
ARG NGINX_VERSION=1.30.7

FROM node:${NODE_VERSION}-bookworm-slim AS build
ARG NPM_VERSION=12.2.3
WORKDIR /app

RUN --mount=type=cache,target=/root/.npm \
    npm install --global npm@${NPM_VERSION} --no-audit --no-fund

COPY package.json package-lock.json ./
RUN --mount=type=cache,target=/root/.npm \
    npm ci --no-audit --no-fund

COPY . ./
RUN npm run build-prod

FROM nginx:${NGINX_VERSION}-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/ /usr/share/nginx/html/static/

EXPOSE 80
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD ["wget", "-q", "-O", "/dev/null", "http://127.0.0.1/healthz"]
CMD ["nginx", "-g", "daemon off;"]
