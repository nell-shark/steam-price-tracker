# Build stage
FROM node:20.11.0-alpine as build

WORKDIR /frontend

COPY package.json package-lock.json ./

RUN npm install --silent

COPY .  .

RUN [ "npm", "run", "build" ]

# Run stage
FROM nginx:1.25.3-alpine

COPY --from=build /frontend/dist /usr/share/nginx/html

COPY --from=build /frontend/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
