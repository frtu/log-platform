# log-platform

Full EFK platform for logs and monitoring. EFK stands for :

* [ElasticSearch](https://www.elastic.co/products/elasticsearch)
* [Fluentd](https://www.fluentd.org/architecture)
* [Kibana](https://www.elastic.co/products/kibana)

More on [logs unification](https://www.fluentd.org/blog/unified-logging-layer)

## Structured logs

Logs used to be a long chain of words and events, **requiring a human** to read and interpret. 

With growing volume of logs, we need to give a structure to logs to allow **machine involvement** in crunching and organizing data easy to identify & aggregate.

By definition, each event data model **depends on business** (what you try to achieve), but here are a set of **technical fields** that are required for every logs to have a context & foster deeper analysis.

 
### Execution context location

Allow to tag every logs sent to EFK with following information :

| Field name   | Definition                               | Example                        | Default value |
|--------------|------------------------------------------|--------------------------------|---------------|
| REGION       | Physical location                        | US\_East\_A, CN\_SHA, ..       | SINGLE        |
| ZONE         | Logical location                         | ZONE\_A, ZONE\_25, ..          | SINGLE        |
| MACHINE_ID   | Specific virtualized ID (like Docker ID) | 239411039fee, 8b9a6863c720, .. | UNKNOWN       |
| SERVICE_NAME | Business component name                  | UserGatewayService, ..         | UNKNOWN       |
| VERSION_TAG  | Specific version or tag                  | service-a:0.0.1-SNAPSHOT       | UNKNOWN       |

### Distributed tracing

Using OpenTracing & Jaeger

* Trace ID : 558907019132e7f8, ..
* Specific Keys (logs) : TXN-123567, PERSIST-67890, ..
* Business Data (logs) : username, ..

Should have an API to

* [addTraceId(final io.opentracing.Span span)](https://github.com/frtu/log-platform/blob/master/logger/src/main/java/com/github/frtu/logs/tracing/LogEnricher.java#L16) : enrich log with Trace ID using a [span](https://opentracing.io/docs/overview/spans/)
* [addKey(String keyName, PrimitiveOrObject value)](https://github.com/frtu/log-platform/blob/master/logger/src/main/java/com/github/frtu/logs/tracing/LogEnricher.java#L28-L44) : enrich log with a specific key
* [addData(String fieldName, PrimitiveOrObject value)](https://github.com/frtu/log-platform/blob/master/logger/src/main/java/com/github/frtu/logs/tracing/LogEnricher.java#L56-L72) : enrich log with some data


## Context passing

### Dev local

When starting an standalone Main class, also add the following :

```
-DREGION=FR -DZONE=A -DSERVICE_NAME=service-a -DMACHINE_ID=982d2ff1686a -DVERSION_TAG=service-a:0.0.1-SNAPSHOT -DJAEGER_ENDPOINT=http://localhost:14268/api/traces
```

### Inside container & docker-compose

* [REGION](https://github.com/frtu/log-platform/blob/master/sample-microservices/.env#L7)
* [ZONE](https://github.com/frtu/log-platform/blob/master/sample-microservices/.env#L8)
* [VERSION_TAG](https://github.com/frtu/log-platform/blob/master/sample-microservices/.env#L9-L10)


## Infrastructure

[Details for development & production env](https://docs.fluentd.org/container-deployment/docker-logging-driver#development-environments)

### With Docker Compose (dev local)

* [EFK Docker Compose](https://docs.fluentd.org/container-deployment/docker-compose) : Using Elastic Search & Kibana OSS (Open source under Apache 2.0 license)

### With K8S (production)

* [fluentd configuration with kubernetes](https://docs.fluentd.org/container-deployment/kubernetes)
* [Basic logging with Kubernetes guide](https://kubernetes.io/docs/concepts/cluster-administration/logging/)
* [High availability guide](https://docs.fluentd.org/deployment/high-availability)

## Using EFK

* [fluentd configuration docs](https://docs.fluentd.org/v1.0/articles/config-file)
* [Enable Kibana configuration guidelines](https://docs.fluentd.org/container-deployment/docker-compose#step-4-confirm-logs-from-kibana)

## Log sources

### Simple HTTP source (test)

* Send data using [http://localhost:9880/myapp.access?json={"event":"data"}](http://localhost:9880/myapp.access?json={"event":"data"})

### From Docker instances

* [Docker logging driver with fluentd](https://docs.docker.com/config/containers/logging/fluentd/)

### Log access from Httpd or Apache

* [httpd docker driver](https://github.com/frtu/log-platform/blob/master/infra/docker/docker-compose.yml#L63-L67)
* [Send Apache logs to MongoDB](https://docs.fluentd.org/how-to-guides/apache-to-mongodb)

### Java log library

fluentd provide a [dedicated java logger](https://docs.fluentd.org/language-bindings/java) but for better integration through [SLF4J](http://www.slf4j.org/) it is recommended to use an adapter to [logback](http://logback.qos.ch/) :

* [sndyuk logback-more-appenders](http://sndyuk.github.io/logback-more-appenders/)


