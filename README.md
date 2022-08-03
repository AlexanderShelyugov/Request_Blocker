# Request Blocker

Blocks incoming requests from the same IP address, if there are too many of them.

![CD pipeline](https://github.com/AlexanderShelyugov/Request_Blocker/actions/workflows/heroku.yml/badge.svg)
![Health-Heroku](https://img.shields.io/website?label=App%20on%20Heroku&url=https://request-blocker.herokuapp.com/actuator/health)

## Table of contents

- [Design](#Design)
    - [Explanation](#Explanation)
    - [Important note!](#important-note)
    - [Achieved bullet points](#achieved-bullet-points)
- [Usage](#Usage)
- [Build](#Build)
- [Run](#Run)

## Design

### Explanation

Since we have a hypothetical high-load application we need to tune counters' read AND write operations.

Since reads and writes are 1-1 we should focus on writes.

The best known way to optimize write operations is **sharding**.

💡 Therefore, we use organized shards!

There are two storage options to use:

- with simple [HashMaps](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/HashMap.html) (faster,
  but drains this process' RAM)
- with [Hazelcast](https://hazelcast.com) (slower, but RAM is used in a separate process + all Hazelcast configuration
  options)

Default option is simple storage. You can use Hazelcast via `storage-hazelcast` Spring profile.

### Important note!

Both [@Configuration](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/Configuration.html)
and [@Component](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/stereotype/Component.html)
bean creation mechanisms are present. This is done intentionally for
demonstration purposes.

### Achieved bullet points

✔️ Limitations and shard sizes are configured in [these](src/main/resources/application.yml) properties.

✔️ Can be added quickly, like
in [this](src/main/java/ru/alexander/request_blocker/web_server/service/impl/SomeProtectedServiceImpl.java) class.

✔️ Best practices are applied to make this functionality fast in high-load applications.

✔️ [Unit test that calls endpoint from random IP addresses in parallel](src/test/java/ru/alexander/request_blocker/web_server/controller/BlankSampleControllerTest.java)
is present.

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
