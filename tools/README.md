# Tools

Tools for Logging, Metrics & Operations.

## spring-boot-admin-web

Based on [https://www.baeldung.com/spring-boot-admin](https://www.baeldung.com/spring-boot-admin)

### Package

Build the docker image using ```mvn verify``` .

You will find the image at ```spring-boot-admin-web:latest```

### Starting Admin Server

Quickly start a spring-boot-admin using [spring-boot-admin-web](spring-boot-admin-web)

```docker run -d --name spring-boot-admin-web -p 8088:8088 -P spring-boot-admin-web```
```docker inspect spring-boot-admin-web```
```docker image rm -f spring-boot-admin-web:latest```

Reach it out using [http://localhost:8088](http://localhost:8088) OR behind a reverse proxy.


### Enable Client

Enable linking to **spring-boot-admin-web** with this [sample PR](https://github.com/frtu/log-platform/commit/10b09b64ddd9164e6ad3651589b000f9bc69acb4)