FROM node:16.14.0-alpine AS builder

WORKDIR /usr/src/app
COPY package.json ./
RUN npm install
COPY . .

RUN npm run build --prod

FROM nginx:1.21.6-alpine
COPY --from=builder /usr/src/app/dist/mancala-frontend/ /usr/share/nginx/html

EXPOSE 80
