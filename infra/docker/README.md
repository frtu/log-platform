# log-platform/docker

Provide a full log-platform startup using docker-compose.

## Jaeger

Access Jaeger interface using : [http://localhost:16686/](http://localhost:16686/)

## Grafana

Access Grafana interface using : [http://localhost:3000/](http://localhost:3000/) admin/admin

Configuration :

* Go to Confiugration > Data sources
* Add prometheus
* Configure URL : `http://prometheus.observability:9090`

## Prometheus

Access Prometheus interface using : [http://localhost:9090/](http://localhost:9090/)

## fluentd

Log into fluentd using : ```localhost:24224```

## Kibana

Access Jaeger interface using : [http://localhost:5601/](http://localhost:5601/)

