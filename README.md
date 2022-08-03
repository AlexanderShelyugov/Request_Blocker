# Request_Blocker

Block incoming requests from the same IP address, if there are too many of them

![CD pipeline](https://github.com/AlexanderShelyugov/Convertio/actions/workflows/heroku.yml/badge.svg)
![Health-Heroku](https://img.shields.io/website?label=App%20on%20Heroku&url=https://request-blocker.herokuapp.com/actuator/health)

## Table of contents

- [Usage](#Usage)
- [Build](#Build)
- [Run](#Run)

## Usage

Protected endpoints have a limit of 3 requests from one IP address within 30 seconds.

Available endpoints:

- [/sample_ip_protected](https://request-blocker.herokuapp.com/sample_ip_protected) - IP-protected endpoint
- [/sample_ip_unprotected](https://request-blocker.herokuapp.com/sample_ip_unprotected) - protection is disabled
- [/shared_service_a](https://request-blocker.herokuapp.com/shared_service_a)
  and  [/shared_service_b](https://request-blocker.herokuapp.com/shared_service_b) - Both endpoints share underlying
  limit checker. The limitation is the same.

We also have [actuators](https://request-blocker.herokuapp.com/actuator):

- [/health](https://request-blocker.herokuapp.com/actuator/health) - basic info about services' health
- [/metrics](https://request-blocker.herokuapp.com/actuator/metrics) - metrics of the app
- [/prometheus](https://request-blocker.herokuapp.com/actuator/prometheus) - addition to the time series for monitoring
  via Prometheus

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
