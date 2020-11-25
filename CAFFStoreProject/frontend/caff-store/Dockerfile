FROM alpine:3.12

RUN apk update && apk add npm
RUN npm install -g @angular/cli

COPY ./ /usr/src/app

WORKDIR /usr/src/app

RUN npm install && ng update && npm update

EXPOSE 4200 49153
