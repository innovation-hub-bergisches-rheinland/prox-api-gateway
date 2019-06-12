# Prox API Gateway

The purpose of this service is to provide routing capabilities to other services. The API gateway
acts as a single entrypoint for multiple services.

## Installation

```
mvn clean install
```

Executes the
[Maven default lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)
up to the install phase. During package phase a runnable JAR is created and during install phase a
docker image is build.

## Usage

```
docker-compose -f docker-compose-api-gateway.yml up
```

Starts a Docker container based on the compose file and the image. A Docker network named `prox` is
required for the communication between services:

```
docker network create prox
```

## About the Team

This service is currently developed by members of the ArchiLab staff:

- Julian Lengelsen ([@jlengelsen](https://github.com/jlengelsen))
