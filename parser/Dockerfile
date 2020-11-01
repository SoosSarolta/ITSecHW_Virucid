FROM alpine:latest

RUN apk update && apk add g++ valgrind afl make

RUN mkdir -p /usr/src/app

COPY . ./usr/src/app

WORKDIR /usr/src/app
