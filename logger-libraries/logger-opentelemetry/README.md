# Project - opentelemetry

## About

```logger-opentelemetry``` is the auto-instrumentation for [OpenTelemetry](https://opentelemetry.io/docs/) library. It relies on the [java version](https://opentelemetry.io/docs/java/) to export Tracing, Metrics and Logs (Current version has Log as Experimental stage).

### History

This library is the [migrated version](logger-libraries/logger-opentracing-fluentd) from deprecated OpenTracing.

![Observability APIs](/docs/img/Observability-API.png)

## logger-opentelemetry

Import using :

```XML
<properties>
  <frtu.logger.version>LATEST_VERSION</frtu.logger.version>
</properties>

<dependency>
  <groupId>com.github.frtu.logs</groupId>
  <artifactId>logger-opentelemetry</artifactId>
  <version>${frtu.logger.version}</version>
</dependency>
```
