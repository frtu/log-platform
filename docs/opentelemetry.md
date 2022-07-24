# OpenTelemetry

Monitoring guide with [OpenTelemetry](https://opentelemetry.io/docs/)

## For Java

* Integration with Java : https://github.com/open-telemetry/opentelemetry-java
* Instrumentation : https://github.com/open-telemetry/opentelemetry-java-instrumentation
* Supported libraries : https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/main/docs/supported-libraries.md
* Release repo : https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases

### Starting up

* Quick start using : [Getting started](https://opentelemetry.io/docs/instrumentation/java/getting-started/#run-the-instrumented-example)
* [Download latest agent](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar)

#### Activate Agent

From pull to agent mode : https://grafana.com/blog/2022/05/04/how-to-capture-spring-boot-metrics-with-the-opentelemetry-java-instrumentation-agent/

[Variable autoconfigure](https://github.com/open-telemetry/opentelemetry-java/blob/main/sdk-extensions/autoconfigure/README.md)

* Using Java command lines

```
APP_NAME=application.name
AGENT_PATH=/PATH_TO/log-platform/sample-microservices/libs

JAVA_OPTS="${JAVA_OPTS} \
    -Xms${JAVA_XMS} \
    -Xmx${JAVA_XMX} \
    -Dapplication.name=${APP_NAME} -Dotel.service.name=${APP_NAME} -Dotel.resource.attributes="service.name=${APP_NAME}"\
    -Dotel.traces.exporter=otlp -Dotel.metrics.exporter=otlp \ 
    -Dotel.exporter.otlp.endpoint=http://localhost:4317 \
    -javaagent:${AGENT_PATH}/opentelemetry-javaagent-1.16.0.jar"
```

* Using env variables

```
OTELCOL_IMG=otel/opentelemetry-collector:latest
OTELCOL_OPTIONS="--config=./config/otel-collector-config.yaml"
OTELCOL_ARGS=

OTEL_TRACES_EXPORTER=otlp,logging
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=http://localhost:4317
OTEL_EXPORTER_OTLP_TRACES_PROTOCOL=http/protobuf
```

#### Toggles

-Dotel.javaagent.debug=true

https://opentelemetry.io/docs/instrumentation/java/automatic/agent-config/#suppressing-specific-auto-instrumentation

```
-Dotel.javaagent.enabled=false
-Dotel.instrumentation.[name].enabled=false
```
