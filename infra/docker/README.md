# log-platform/docker

Provide a full log-platform startup using docker-compose.

## Jaeger

Access Jaeger interface using : [http://localhost:16686/](http://localhost:16686/)

Data should be pushed to 

* gRPC : `localhost:14250`

## Grafana

Access Grafana interface using : [http://localhost:3000/](http://localhost:3000/) admin/admin

Configuration :

* Go to Configuration > Data sources
* Add `Prometheus`
* Configure URL : `http://prometheus.observability:9090`
* Click `Save & test` and then `Expore`

## Prometheus

Access Prometheus interface using : [http://localhost:9090/](http://localhost:9090/)

## Kibana

Access Jaeger interface using : [http://localhost:5601/](http://localhost:5601/)

## OTEL

Please configure OTEL scraper in [otel-collector-config.yaml](otel/otel-collector-config.yaml) :

* Replace `HOST_IP` with your local IP (force to use bridge instead of internal docker localhost)
* Add tags using `processors.batch.resource.attributes`
* In `service` enable/disable OTEL `logging`
* Enable infra own metrics

## fluentd

Log into fluentd using : ```localhost:24224```
