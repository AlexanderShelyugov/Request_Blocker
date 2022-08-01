# Request_Blocker

Block incoming requests from the same IP address, if there are too much of them

## Table of contents

- [Usage](#Usage)
- [Build](#Build)
- [Run](#Run)

## Usage

TODO

## Build

### Gradle

```shell
gradle build
```

### Docker

```shell
docker build -t request_blocker_web_service .
```

## Run

### Execute Jar

```shell
java -jar build/libs/request_blocker.jar
```

### Run Docker container

```shell
docker run --rm -p 8080:8080 request_blocker_web_service
```