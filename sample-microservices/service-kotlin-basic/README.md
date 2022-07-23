# Project - service-kotlin-basic

## About

Basic project 

* providing `subscriptions` with related R2DBC database.
* exposing Subscription API at [http://localhost:8081/v1/subscriptions](http://localhost:8081/v1/subscriptions) 

### Async with WebFlux

* Access Swagger API at [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

## Integration with Docker

Build your Docker image at ```mvn verify``` phase

Startup your application using :

```
docker run -d --name <instance-name> -p 8080:8080 -P <your-archetype-id>
```

* -d : startup as a daemon
* --name : give it a name

## Release notes

### 0.0.1-SNAPSHOT - Current version

* Feature list