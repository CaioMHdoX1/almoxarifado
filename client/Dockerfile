# ---- Etapa de build --------------------------------------------------------
FROM node:20-alpine AS build
WORKDIR /build

COPY package.json package-lock.json* ./
RUN npm install

COPY . .

# VITE_API_URL precisa existir NO MOMENTO DO BUILD — variáveis do Vite são
# "queimadas" no bundle estático, não lidas em runtime como no backend.
# Pode ser sobrescrita via `docker compose build --build-arg VITE_API_URL=...`
ARG VITE_API_URL=http://localhost:8080
ENV VITE_API_URL=$VITE_API_URL

RUN npm run build

# ---- Etapa de runtime -------------------------------------------------------
FROM nginx:1.27-alpine
COPY --from=build /build/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
