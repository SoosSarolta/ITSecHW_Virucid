FROM alpine:3.11

RUN apk update && apk add openjdk11 maven g++ make

COPY ./ usr/src/app

ENV JAVA_HOME /usr/lib/jvm/default-jvm
RUN export JAVA_HOME

EXPOSE 8080
