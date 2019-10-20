# log-platform

Full EFK platform for logs and monitoring. EFK stands for :

* [ElasticSearch](https://www.elastic.co/products/elasticsearch)
* [Fluentd](https://www.fluentd.org/architecture)
* [Kibana](https://www.elastic.co/products/kibana)

More on [logs unification](https://www.fluentd.org/blog/unified-logging-layer)

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


