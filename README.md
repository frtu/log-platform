# log-platform

Full EFK platform for logs and monitoring. EFK stands for :

* [ElasticSearch](https://www.elastic.co/products/elasticsearch)
* [Fluentd](https://www.fluentd.org/architecture)
* [Kibana](https://www.elastic.co/products/kibana)

More on [logs unification](https://www.fluentd.org/blog/unified-logging-layer)

On tracing using :

* [OpenTracing for CNCF](https://opentracing.io/)
* [Jaeger implementation](https://www.jaegertracing.io/docs/1.15/)

Note : [OpenTelemetry](https://medium.com/jaegertracing/jaeger-and-opentelemetry-1846f701d9f2) will be replacing OpenTracing, nevertheless it is **not ready and stable as of now**. Expect also implementation to be fully stable before migrating to the latest version of Jaeger.

## Structured logs

Logs used to be a long chain of words and events, **requiring a human** to read and interpret. 

With growing volume of logs, we need to give a structure to logs to allow **machine involvement** in crunching and organizing data easy to identify & aggregate.

By definition, each event data model **depends on business** (what you try to achieve), but here are a set of **technical fields** that are required for every logs to have a context & foster deeper analysis.

 
### Execution context location

Allow to tag every logs sent to EFK with following information :

| Field name   | Definition                               | Example                        | Default value |
|--------------|------------------------------------------|--------------------------------|---------------|
| [REGION](https://github.com/frtu/log-platform/blob/master/logger/src/main/resources/logback-appenders-fluentd.xml#L23-L26)       | Physical location                        | US\_East\_A, CN\_SHA, ..       | SINGLE        |
| [ZONE](https://github.com/frtu/log-platform/blob/master/logger/src/main/resources/logback-appenders-fluentd.xml#L27-L30)         | Logical location                         | ZONE\_A, ZONE\_25, ..          | SINGLE        |
| [MACHINE_ID](https://github.com/frtu/log-platform/blob/master/logger/src/main/resources/logback-appenders-fluentd.xml#L35-L38)   | Specific virtualized ID (like Docker ID) | 239411039fee, 8b9a6863c720, .. | UNKNOWN       |
| [SERVICE_NAME](https://github.com/frtu/log-platform/blob/master/logger/src/main/resources/logback-appenders-fluentd.xml#L31-L34) | Business component name                  | UserGatewayService, ..         | UNKNOWN       |
| [VERSION_TAG](https://github.com/frtu/log-platform/blob/master/logger/src/main/resources/logback-appenders-fluentd.xml#L39-L42)  | Specific version or tag                  | service-a:0.0.1-SNAPSHOT       | UNKNOWN       |

### Distributed tracing

| Field name   | Definition                               | Example                        | Default value |
|--------------|------------------------------------------|--------------------------------|---------------|
| [TRACE_ID](https://github.com/frtu/log-platform/blob/master/logger/src/main/resources/logback-appenders-fluentd.xml#L43-L46)       | Unique ID per Request                        | 558907019132e7f8, ..       | [NULL]        |


* Trace ID : 558907019132e7f8, ..
* Specific Keys (logs) : TXN-123567, PERSIST-67890, ..
* Business Data (logs) : username, ..


### StructuredLogger

#### Logs

Allow to create new dimension in ElasticSearch. Initialize the logger similar with Slf4j LOGGER :

```java
final static StructuredLogger STRUCTURED_LOGGER = StructuredLogger.create("usage");
```

Then use it for logging String or Integer values :

```java
STRUCTURED_LOGGER.info(entry("key1", "value1"), entry("key2", "value2"));
STRUCTURED_LOGGER.info(entry("key1", 123), entry("key2", 456));
```

Gives a JSON log :

```json
{"key1":"value1","key2":"value2"}
{"key1":123,"key2":456}
```

### RpcLogger

Implementation to logs RPC calls, in a generic way :

* RESTful
* GraphQL
* etc..

#### GraphQL sample

Logging API errors :

```java
rpcLogger.warn(client(),
    method("query"),
    uri("/HeroNameAndFriends"),
    statusCode("123"),
    errorMessage("The invitation has expired, please request a new one")
);
```

Gives a log :

* kind : client | server
* method : query | mutation | subscription
* uri : String
* response_code

```json
{
   "kind":"client",
   "method":"Query",
   "uri":"/HeroNameAndFriends",
   "response_code":"123",
   "error_message":"The invitation has expired, please request a new one"
}
```

#### Full guide for Structured Logging

[Full guide for Structured Logging](docs/guide_for_structured_logging.md)

## Adoption

Import using :

```XML
<dependency>
    <groupId>com.github.frtu.logs</groupId>
    <artifactId>logger-core</artifactId>
    <version>${frtu-logger.version}</version>
</dependency>
```

Check the latest version (clickable) :

[<img src="https://img.shields.io/maven-central/v/com.github.frtu.logs/logger-core.svg?label=latest%20release%20:%20logger-core"/>](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22logger-core%22+g%3A%22com.github.frtu.logs%22)

### Configure logback

* Choose any template from ```logger-core/src/main/resources/templates/```
* Copy into your ```src/main/resources/``` folder

#### fluentd in logback-spring.xml

When using logback-spring.xml, you can override any logback ENV with Spring properties using :

```xml
<springProperty scope="context" name="SERVICE_NAME" source="application.name"/>
```

In your property file, just configure fluentd 

* tag.label
* region
* zone
* etc.

```properties
fluentd.tag=tag
fluentd.label=label
logging.region=localhost
logging.zone=zone
logging.path=target/
```

#### file appender in logback-spring.xml

For Production & avoid message loss, it is recommended to use log file + fluentd tail (instead of streaming log) to allow local buffering.

Define log file location with system env :

* ```$LOG_PATH/$SERVICE_NAME.log```
* or ```LOG_FILE_LOCATION```

In your application properties or yaml :

```properties
logging.path=target/
```

Also configure RollingFileAppender using :

```xml
<property name="LOG_FILE_MAX_SIZE" value="${LOG_FILE_MAX_SIZE:-5MB}"/>
<property name="LOG_FILE_MAX_HISTORY" value="${LOG_FILE_MAX_HISTORY:-15}"/>
<property name="LOG_FILE_MAX_TOTAL_SIZE" value="${LOG_FILE_MAX_SIZE:-100MB}"/>
```

### Log forwarder

#### Enablement

Import logback configuration from [templates folder](https://github.com/frtu/log-platform/tree/master/logger-libraries/logger-core/src/main/resources/templates) for :

* Standalone application : [logback.xml](https://github.com/frtu/log-platform/blob/master/logger-libraries/logger-core/src/main/resources/templates/logback.xml)
* Spring-Boot application (including [profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#profile-specific-configuration)) : [logback-spring.xml](https://github.com/frtu/log-platform/blob/master/logger-libraries/logger-core/src/main/resources/templates/logback-spring.xml)


For troubleshooting, add the import to flush fluentd config into log :

```java
@ComponentScan(basePackages = {"com.github.frtu.logs.infra.fluentd", "..."})
```

#### Usage

Just log with logback, activate FLUENT appender on Staging or Production.


### a) Core Tracer API

#### Enablement

If you only need Jaeger io.opentracing.Tracer, just add :

```java
@ComponentScan(basePackages = {"com.github.frtu.logs.tracing.core", "..."})
```

#### Usage
You can create a single Span structure :

```java
Span span = tracer.buildSpan("say-hello1").start();
LOGGER.info("hello1");
span.finish();
```

OR a node from a graph using Scope :

```java
try (Scope scope = tracer.buildSpan("say-hello2").startActive(true)) {
	LOGGER.info("hello2");
}
```


* See [sample-microservices/service-a](https://github.com/frtu/log-platform/tree/master/sample-microservices/service-a) or [ChangeList](https://github.com/frtu/log-platform/commit/57ee4d99b3a219cc662c710726353a239e02b035)
* Or more at [opentracing.io - span](https://opentracing.io/docs/overview/spans/)

### b) @ExecutionSpan AOP

#### Enablement

If you want to use @ExecutionSpan to mark a method to create Span, add :

```java
@ComponentScan(basePackages = {"com.github.frtu.logs.tracing", "..."})
```

And add Spring AOP :

```XML
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
</dependency>
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
</dependency>
```

OR spring-boot AOP :

```XML
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

#### Basic usage

Just annotate with @ExecutionSpan all the methods you need to create a [DAG](https://en.wikipedia.org/wiki/Directed_acyclic_graph) :

```java
@ExecutionSpan
public String method() {}
```

You can optionally add a Spring property to get a full classname trace :

```
trace.full.classname=true
```

See [sample-microservices/service-b](https://github.com/frtu/log-platform/tree/master/sample-microservices/service-b) or [ChangeList](https://github.com/frtu/log-platform/commit/c9e47df45922073a95b24193291bd662064ae381)

#### Tag & Log enrichment

To add Tag use :

```java
@ExecutionSpan({
        @Tag(tagName = "key1", tagValue = "value1"),
        @Tag(tagName = "key2", tagValue = "value2")
})
public void method() {}
```

To add Log use :

```java
@ExecutionSpan
public String method(@ToLog("paramName") String param) {}
```

### Manually add Span.log

Use spring @Autowired to get instance of com.github.frtu.logs.tracing.core.TraceHelper :

```java
@Autowired
private TraceHelper traceHelper;

void method() {
	traceHelper.addLog("log1", "value1");
}
```

## Context passing

### Dev local

When starting an standalone Main class, also add the following to VM options :

```
-DREGION=FR -DZONE=A -DSERVICE_NAME=service-a -DMACHINE_ID=982d2ff1686a -DVERSION_TAG=service-a:0.0.1-SNAPSHOT
```

Also add Jaeger Configuration for :

* HTTP : ```-DJAEGER_ENDPOINT=http://localhost:14268/api/traces```
* Agent : ```-DJAEGER_AGENT_HOST=localhost -DJAEGER_AGENT_PORT=6831```

### Inside container & docker-compose

* [REGION](https://github.com/frtu/log-platform/blob/master/sample-microservices/.env#L7)
* [ZONE](https://github.com/frtu/log-platform/blob/master/sample-microservices/.env#L8)
* [VERSION_TAG](https://github.com/frtu/log-platform/blob/master/sample-microservices/.env#L9-L10)


Go to folder **/sample-microservices/** and ```docker-compose up```

## Metrics

### Adoption

#### Import the JAR

```XML
<dependency>
    <groupId>com.github.frtu.logs</groupId>
    <artifactId>logger-metrics</artifactId>
    <version>${frtu-logger.version}</version>
</dependency>
```

Check the latest version (clickable) :

[<img src="https://img.shields.io/maven-central/v/com.github.frtu.logs/logger-metrics?label=latest%20release%20:%20logger-metrics"/>](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22logger-metrics%22+g%3A%22com.github.frtu.logs%22)

#### Spring Annotation

Import Spring Configuration :

```java
@Import({MetricsConfig.class, ...})
```

#### Spring Properties

```properties
# =================================
# Metrics related configurations
# =================================
# https://www.callicoder.com/spring-boot-actuator/
management.endpoints.web.exposure.include=*

management.endpoint.health.show-details=always

management.endpoint.metrics.enabled=true
management.endpoint.prometheus.enabled=true

management.metrics.export.prometheus.enabled=true
```

### Custom measurement

This library provide a class to abtract from direct Counter & Timer :

* *com.github.frtu.metrics.micrometer.model.Measurement*

```java
final Iterable<Tag> tags = ...;

final Measurement measurement = new Measurement(registry, operationName);
measurement.setOperationDescription(operationDescription);
measurement.setTags(tags);

try (MeasurementHandle handle = new MeasurementHandle(measurement)) {
    return joinPoint.proceed();
} catch (Throwable ex) {
    throw MeasurementHandle.flagError(ex);
}
```

### Dashboard

Grafana Dashboard :

* [JVM (Micrometer) - 4701](https://grafana.com/grafana/dashboards/4701)
* [Spring Boot 2.1 Statistics - 10280](https://grafana.com/grafana/dashboards/10280)

### See more

* [micrometer.io prometheus installation](http://micrometer.io/docs/registry/prometheus#_installing)

## Tools & Tips

### Runtime changing the level

[Dynamically changing spring-boot application logs LEVEL](https://www.baeldung.com/spring-boot-changing-log-level-at-runtime)

* Using Actuator => org.springframework.boot:**spring-boot-starter-actuator**

```
management.endpoints.web.exposure.include=loggers
management.endpoint.loggers.enabled=true
```

Can also use the shell scripts at [bash-fwk/lib-dev-spring](https://github.com/frtu/bash-fwk/blob/master/libs/README.md#library-dev-spring)

### Operation tools

Check [Tools](tools) module.

## Infrastructure

[Details for development & production env](https://docs.fluentd.org/container-deployment/docker-logging-driver#development-environments)

### With Docker Compose (dev local)

* [EFK Docker Compose](https://docs.fluentd.org/container-deployment/docker-compose) : Using Elastic Search & Kibana OSS (Open source under Apache 2.0 license)

#### URLs

Monitoring

* Grafana : [http://localhost:3000/](http://localhost:3000/)
* Prometheus : [http://localhost:9090/](http://localhost:9090/)
* Prometheus Targets : [http://localhost:9090/targets](http://localhost:9090/)

Distributed Tracing :

* Jaeger : [http://localhost:16686/](http://localhost:16686/)

Logging :

* Kibana : [http://localhost:5601/](http://localhost:5601/)

Tools :

* Spring Admin : [http://localhost:8888/](http://localhost:8888/)

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

## See also

* Get familiar with the concepts with [Observability 3 ways: Logging, Metrics & Tracing](https://www.dotconferences.com/2017/04/adrian-cole-observability-3-ways-logging-metrics-tracing) by Adrian Cole
* [opentelemetry-beyond-getting-started](https://medium.com/opentelemetry/opentelemetry-beyond-getting-started-5ac43cd0fe26)

