# RxWeb

## Introduction

A simple example [Vert.x](vertx.io) application using RxJava2 in Kotlin. Connects
to the [Transport for London (TFL)](https://api.tfl.gov.uk/) Unified API to list
underground lines, stations and train arrival times. Implements a REST API and a
streaming/push API (does not currently connect to TFL push API, but polls their
REST API at regular intervals) using Server Sent Events.

## Libraries

Uses [vertx-sse](https://github.com/aesteve/vertx-sse), which is currently
unpublished and will need to be built and installed to a local repository. The
easiest way to run the application is to clone the repository and import it into
IntelliJ, ensuring the JVM version is set to 1.8, and running the Gradle run
configuration.

## Usage

### Streaming/Push API

The following endpoints are available:

- `localhost:8080/push-api/underground/lines`
- `localhost:8080/push-api/underground/lines/:line/stations`
- `localhost:8080/push-api/underground/lines/:line/:station/arrivals`

To access the streaming API using `cURL` or `HTTPie`, it is necessary to specify
the relevant `Accept` headers, eg:

    curl localhost:8080/push-api/underground/lines -H Accept:text/event-stream
    http localhost:8080/push-api/underground/lines Accept:text/event-stream --stream

### REST API

The following endpoints are available:

- `localhost:8080/api/underground/lines`
- `localhost:8080/api/underground/lines/:line/stations`
- `localhost:8080/api/underground/lines/:line/:station/arrivals`

These endpoints can be accessed with `cURL` or `HTTPie` in the usual way, e.g:

    curl -i -X GET localhost:8080/api/underground/lines
    http localhost:8080/api/underground/lines