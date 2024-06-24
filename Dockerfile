# syntax=docker/dockerfile:1
FROM openjdk:17-jdk-slim

WORKDIR /server

COPY . /server/
