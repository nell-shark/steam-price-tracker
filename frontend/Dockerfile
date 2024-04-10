# Build stage
FROM node:20.11.0-alpine AS build

ARG VITE_CAPTCHA_SITE_KEY

ENV VITE_CAPTCHA_SITE_KEY=$VITE_CAPTCHA_SITE_KEY

WORKDIR /frontend

COPY package.json package-lock.json ./

RUN npm install --silent

COPY .  .

RUN [ "npm", "run", "build" ]

# Run stage
FROM nginx:1.25.3-alpine

COPY --from=build /frontend/dist /usr/share/nginx/html

COPY --from=build /frontend/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
