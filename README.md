# Prox API Gateway

The purpose of this service is to provide routing capabilities to other services. The API gateway
acts as a single entrypoint for multiple services.

#### A note on automatic formatting and linting

We partially use the Node.js runtime and npm to introduce a consistent code formatting in the whole
project. Doing so Maven will download and install Node and npm into the project directory using the
[frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin)
and runs `npm ci`. This will install [scripts](./.husky) using husky into your git hooks.
We understand that this process might sound malicious to you, and therefore you are free
to disable the behaviour by explicitly disabling the maven profile `node`. Doing so will skip the
installation of node, npm and therefore will prevent installing the git hooks. However, we strongly
encourage you to adapt the process if you plan to contribute to this project as it keeps our
code-base clean and in a consistent style without any additional effort.

## Installation

After a `git clone` or download the project the following command must be executed once to initialize the projects.

Windows (CMD/PowerShell)

```posh
cd .\prox-api-gateway\
# Execute initial build
.\mvnw.cmd clean test
```

Linux/MacOS (Bash/Terminal)

```bash
cd prox-api-gateway/
# Execute initial build
./mvnw clean test
```

Executes the [Maven default lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html) up to the `test` phase.

## Building

```bash
./mvnw clean package
```

During the `package` phase, an executable JAR and the Docker image are created.

## Local usage with docker

A Docker network named `prox` is required for the communication between services:

```bash
docker network create prox
```

Starts a Docker container based on the compose file and the image.

Powershell

```posh
$env:IMAGE='prox-api-gateway'; `
$env:TAG='latest'; `
docker-compose -f ./src/main/docker/docker-compose.yml up
```

Bash/Shell

```bash
export IMAGE="prox-api-gateway" &&
export TAG="latest" &&
docker-compose -f ./src/main/docker/docker-compose.yml up
```

## About the Team

This service is currently developed by members of the ArchiLab staff:

- Julian Lengelsen ([@jlengelsen](https://github.com/jlengelsen))
- Rudolf Grauberger ([@rudolfgrauberger](https://github.com/rudolfgrauberger))
