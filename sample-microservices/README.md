# sample-microservices

Spring Boot sample applications for logging demo

## Build and run

### Build

Create 2 docker images using : ```mvn clean package```

Verify build succesful by running ```docker images```. You should be able to see 2 new Spring boot application built :

* service-a:${project.version}
* service-b:${project.version}

### Run

Run : ```docker-compose up```

You should be able to see both application running on :

* Service A : [http://localhost:8082/](http://localhost:8081/)
* Service B : [http://localhost:8082/](http://localhost:8082/)