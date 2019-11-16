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
| [REGION](https://github.com/frtu/log-platform/blob/master/logger/src/main/resources/logback-appenders-fluentd.xml#L23-L26)       | Physical location                        | US\_East\_A, CN\_SHA, ..       | SINGLE        |
| [ZONE](https://github.com/frtu/log-platform/blob/master/logger/src/main/resources/logback-appenders-fluentd.xml#L27-L30)         | Logical location                         | ZONE\_A, ZONE\_25, ..          | SINGLE        |
| [MACHINE_ID](https://github.com/frtu/log-platform/blob/master/logger/src/main/resources/logback-appenders-fluentd.xml#L35-L38)   | Specific virtualized ID (like Docker ID) | 239411039fee, 8b9a6863c720, .. | UNKNOWN       |
| [SERVICE_NAME](https://github.com/frtu/log-platform/blob/master/logger/src/main/resources/logback-appenders-fluentd.xml#L31-L34) | Business component name                  | UserGatewayService, ..         | UNKNOWN       |
| [VERSION_TAG](https://github.com/frtu/log-platform/blob/master/logger/src/main/resources/logback-appenders-fluentd.xml#L39-L42)  | Specific version or tag                  | service-a:0.0.1-SNAPSHOT       | UNKNOWN       |

### Distributed tracing

Using [OpenTracing](https://opentracing.io/) & [Jaeger](https://www.jaegertracing.io/)

| Field name   | Definition                               | Example                        | Default value |
|--------------|------------------------------------------|--------------------------------|---------------|
| [TRACE_ID](https://github.com/frtu/log-platform/blob/master/logger/src/main/resources/logback-appenders-fluentd.xml#L43-L46)       | Unique ID per Request                        | 558907019132e7f8, ..       | [NULL]        |


* Trace ID : 558907019132e7f8, ..
* Specific Keys (logs) : TXN-123567, PERSIST-67890, ..
* Business Data (logs) : username, ..

##### Adoption

Import using :

```XML
<dependency>
    <groupId>com.github.frtu.logs</groupId>
    <artifactId>logger-core</artifactId>
    <version>${frtu-logger.version}</version>
</dependency>
```

Check the latest version (clickable) :

[<img src="https://img.shields.io/maven-central/v/com.github.frtu.logs/logger-core.svg?label=latest%20release%20:%20logger-core"/>](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22logger-core%22+g%3A%22 com.github.frtu.logs%22)

###### a) Core Tracer API

If you only need Jaeger Tracer, just add :

```
@ComponentScan(basePackages = {"com.github.frtu.logs.tracing.core", "..."})
```

USAGE : You can create a single Span structure :

```
Span span = tracer.buildSpan("say-hello1").start();
LOGGER.info("hello1");
span.finish();
```

OR a node from a graph using Scope :

```
try (Scope scope = tracer.buildSpan("say-hello2").startActive(true)) {
	LOGGER.info("hello2");
}
```


* See [sample-microservices/service-a](https://github.com/frtu/log-platform/tree/master/sample-microservices/service-a) or [ChangeList](https://github.com/frtu/log-platform/commit/57ee4d99b3a219cc662c710726353a239e02b035)
* Or more at [opentracing.io - span](https://opentracing.io/docs/overview/spans/)

###### b) @ExecutionSpan AOP

If you want to use @ExecutionSpan to mark a method to create Span, add :

```
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

USAGE : Just annotate with @ExecutionSpan all the methods you need to create a [DAG](https://en.wikipedia.org/wiki/Directed_acyclic_graph) :

```
@ExecutionSpan
public String method() {}
```

You can optionally add a Spring property to get a full classname trace :

```
trace.full.classname=true
```

See [sample-microservices/service-b](https://github.com/frtu/log-platform/tree/master/sample-microservices/service-b) or [ChangeList](https://github.com/frtu/log-platform/commit/c9e47df45922073a95b24193291bd662064ae381)


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


