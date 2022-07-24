# Monitoring

Monitoring guide with [OpenTelemetry](https://opentelemetry.io/docs/)
* Quick start using : [Getting started](https://opentelemetry.io/docs/instrumentation/java/getting-started/#run-the-instrumented-example)
* [Download latest agent](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar)


Using agent mode : https://grafana.com/blog/2022/05/04/how-to-capture-spring-boot-metrics-with-the-opentelemetry-java-instrumentation-agent/

```
export AGENT_PATH=/PATH_TO/log-platform/sample-microservices/libs
OTEL_TRACES_EXPORTER=otlp,logging

APP_NAME=application.name
JAVA_OPTS="${JAVA_OPTS} \
    -Xms${JAVA_XMS} \
    -Xmx${JAVA_XMX} \
    -Dapplication.name=${APP_NAME} -Dotel.traces.exporter=otlp -Dotel.service.name=service-kotlin-basic -Dotel.exporter.otlp.endpoint=http://localhost:4317
    -javaagent:${AGENT_PATH}/opentelemetry-javaagent-1.16.0.jar"

OTELCOL_IMG=otel/opentelemetry-collector:latest
OTELCOL_OPTIONS="--config=./config/otel-collector-config.yaml"
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=http://localhost:4317
OTEL_EXPORTER_OTLP_TRACES_PROTOCOL=http/protobuf
OTEL_SERVICE_NAME=spring-boot-rest-service
OTEL_RESOURCE_ATTRIBUTES=application=APPLICATION_NAME
OTELCOL_ARGS=
```


-Dotel.javaagent.debug=true

https://opentelemetry.io/docs/instrumentation/java/automatic/agent-config/#suppressing-specific-auto-instrumentation

```
-Dotel.javaagent.enabled=false
-Dotel.instrumentation.[name].enabled=false
```

## Grafana Dashboard

### Add datasource

* Grafana : [http://localhost:3000/](http://localhost:3000/) (Login / Pwd : admin / admin)

Add datasource using Prometheus from Grafana **docker instance** configure access **using DNS** and not localhost !

* [http://prometheus:9090](http://prometheus:9090)

### Import Dashboard

Go to Dashboard > Manage > Import button :

* Type the ID for the desired dashboard (ex : ```4701```)
* Select the previously configured datasource
* Click import

Interesting dashboard :

* [SpringBoot APM Dashboard - 12900](https://grafana.com/grafana/dashboards/12900)
* [Spring Boot Application (including Chaos monkey) - 9845](https://grafana.com/grafana/dashboards/9845)

* [JVM (Micrometer) - 4701](https://grafana.com/grafana/dashboards/4701)
* [Spring Boot 2.1 Statistics - 10280](https://grafana.com/grafana/dashboards/10280)

## Metrics

### Database

#### Spring Data Repository

```
# HELP spring_data_repository_invocations_seconds_max (gauge)
spring_data_repository_invocations_seconds_max
# HELP spring_data_repository_invocations_seconds (summary)
spring_data_repository_invocations_seconds_count
spring_data_repository_invocations_seconds_sum
```

* exception="None",
* method="save",
* repository="IEmailRepository",
* state="SUCCESS" OR state="CANCELED"

#### R2DBC

```
# HELP r2dbc_pool_acquired_connections Size of successfully acquired connections which are in active use. (gauge)
r2dbc_pool_acquired_connections{name="connectionFactory",} 0.0
# HELP r2dbc_pool_max_allocated_connections Maximum size of allocated connections that this pool allows. (gauge)
r2dbc_pool_max_allocated_connections{name="connectionFactory",} 10.0
# HELP r2dbc_pool_idle_connections Size of idle connections in the pool. (gauge)
r2dbc_pool_idle_connections{name="connectionFactory",} 10.0
# HELP r2dbc_pool_allocated_connections Size of allocated connections in the pool which are in active use or idle. (gauge)
r2dbc_pool_allocated_connections{name="connectionFactory",} 10.0
# HELP r2dbc_pool_pending_connections Size of pending to acquire connections from the underlying connection factory. (gauge)
r2dbc_pool_pending_connections{name="connectionFactory",} 0.0
# HELP r2dbc_pool_max_pending_connections Maximum size of pending state to acquire connections that this pool allows. (gauge)
r2dbc_pool_max_pending_connections{name="connectionFactory",} 2.147483647E9
```

### gRPC

Monitoring can be provided using micrometer `MetricCollectingServerInterceptor`

```java / kotlin
import io.micrometer.core.instrument.binder.grpc.MetricCollectingServerInterceptor
```

#### Throughput IN / OUT

```
# HELP grpc_server_requests_received_messages_total The total number of requests received (counter)
grpc_server_requests_received_messages_total
# HELP grpc_server_responses_sent_messages_total The total number of responses sent (counter)
grpc_server_responses_sent_messages_total
```

#### Duration

```
# HELP grpc_server_processing_duration_seconds The total time taken for the server to complete the call (summary)
grpc_server_processing_duration_seconds_count
grpc_server_processing_duration_seconds_sum
# HELP grpc_server_processing_duration_seconds_max The total time taken for the server to complete the call (gauge)
grpc_server_processing_duration_seconds_max
```

### Kafka Sync

Grafana Dashboard : https://grafana.com/grafana/dashboards/16088

Using `@KafkaListener(topics = ["app-subscriptions-2"], groupId = "group")`

```
# HELP kafka_consumer_fetch_manager_bytes_consumed_rate The average number of bytes consumed per second for a topic
# TYPE kafka_consumer_fetch_manager_bytes_consumed_rate gauge
kafka_consumer_fetch_manager_bytes_consumed_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",topic="app-subscriptions-2",} 7.971800433839479
# HELP kafka_consumer_fetch_manager_fetch_throttle_time_avg The average throttle time in ms
# TYPE kafka_consumer_fetch_manager_fetch_throttle_time_avg gauge
kafka_consumer_fetch_manager_fetch_throttle_time_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_coordinator_commit_latency_avg The average time taken for a commit request
# TYPE kafka_consumer_coordinator_commit_latency_avg gauge
kafka_consumer_coordinator_commit_latency_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 13.0
# HELP kafka_consumer_fetch_manager_fetch_size_avg The average number of bytes fetched per request for a topic
# TYPE kafka_consumer_fetch_manager_fetch_size_avg gauge
kafka_consumer_fetch_manager_fetch_size_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",topic="app-subscriptions-2",} 294.0
# HELP kafka_consumer_select_total The total number of times the I/O layer checked for new I/O to perform
# TYPE kafka_consumer_select_total counter
kafka_consumer_select_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 472.0
# HELP kafka_consumer_reauthentication_latency_max The max latency observed due to re-authentication
# TYPE kafka_consumer_reauthentication_latency_max gauge
kafka_consumer_reauthentication_latency_max{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_coordinator_partition_assigned_latency_avg The average time taken for a partition-assigned rebalance listener callback
# TYPE kafka_consumer_coordinator_partition_assigned_latency_avg gauge
kafka_consumer_coordinator_partition_assigned_latency_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_fetch_manager_fetch_total The total number of fetch requests.
# TYPE kafka_consumer_fetch_manager_fetch_total counter
kafka_consumer_fetch_manager_fetch_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 144.0
# HELP kafka_consumer_connection_creation_total The total number of new connections established
# TYPE kafka_consumer_connection_creation_total counter
kafka_consumer_connection_creation_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 3.0
# HELP kafka_consumer_fetch_manager_records_lag The latest lag of the partition
# TYPE kafka_consumer_fetch_manager_records_lag gauge
kafka_consumer_fetch_manager_records_lag{client_id="consumer-group-1",kafka_version="3.1.1",partition="0",spring_id="kafkaConsumerFactory.consumer-group-1",topic="app-subscriptions-2",} 0.0
# HELP kafka_consumer_outgoing_byte_total The total number of outgoing bytes sent to all servers
# TYPE kafka_consumer_outgoing_byte_total counter
kafka_consumer_outgoing_byte_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 12702.0
# HELP kafka_consumer_coordinator_sync_total The total number of group syncs
# TYPE kafka_consumer_coordinator_sync_total counter
kafka_consumer_coordinator_sync_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 1.0
# HELP kafka_consumer_coordinator_partition_revoked_latency_avg The average time taken for a partition-revoked rebalance listener callback
# TYPE kafka_consumer_coordinator_partition_revoked_latency_avg gauge
kafka_consumer_coordinator_partition_revoked_latency_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_fetch_manager_fetch_throttle_time_max The maximum throttle time in ms
# TYPE kafka_consumer_fetch_manager_fetch_throttle_time_max gauge
kafka_consumer_fetch_manager_fetch_throttle_time_max{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_last_poll_seconds_ago The number of seconds since the last poll() invocation.
# TYPE kafka_consumer_last_poll_seconds_ago gauge
kafka_consumer_last_poll_seconds_ago{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 1.0
# HELP kafka_consumer_node_response_total The total number of responses received
# TYPE kafka_consumer_node_response_total counter
kafka_consumer_node_response_total{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-1",spring_id="kafkaConsumerFactory.consumer-group-1",} 148.0
kafka_consumer_node_response_total{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node--1",spring_id="kafkaConsumerFactory.consumer-group-1",} 4.0
kafka_consumer_node_response_total{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-2147483646",spring_id="kafkaConsumerFactory.consumer-group-1",} 31.0
# HELP kafka_consumer_failed_reauthentication_total The total number of failed re-authentication of connections
# TYPE kafka_consumer_failed_reauthentication_total counter
kafka_consumer_failed_reauthentication_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_node_incoming_byte_rate The number of incoming bytes per second
# TYPE kafka_consumer_node_incoming_byte_rate gauge
kafka_consumer_node_incoming_byte_rate{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-1",spring_id="kafkaConsumerFactory.consumer-group-1",} 52.94764829925736
kafka_consumer_node_incoming_byte_rate{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node--1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
kafka_consumer_node_incoming_byte_rate{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-2147483646",spring_id="kafkaConsumerFactory.consumer-group-1",} 5.799520630029105
# HELP kafka_consumer_coordinator_partition_assigned_latency_max The max time taken for a partition-assigned rebalance listener callback
# TYPE kafka_consumer_coordinator_partition_assigned_latency_max gauge
kafka_consumer_coordinator_partition_assigned_latency_max{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_coordinator_rebalance_latency_max The max time taken for a group to complete a successful rebalance, which may be composed of several failed re-trials until it succeeded
# TYPE kafka_consumer_coordinator_rebalance_latency_max gauge
kafka_consumer_coordinator_rebalance_latency_max{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_io_wait_ratio *Deprecated* The fraction of time the I/O thread spent waiting
# TYPE kafka_consumer_io_wait_ratio gauge
kafka_consumer_io_wait_ratio{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.9902787618125
# HELP kafka_consumer_outgoing_byte_rate The number of outgoing bytes sent to all servers per second
# TYPE kafka_consumer_outgoing_byte_rate gauge
kafka_consumer_outgoing_byte_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 160.47803753013943
# HELP kafka_consumer_io_wait_time_ns_avg The average length of time the I/O thread spent waiting for a socket ready for reads or writes in nanoseconds.
# TYPE kafka_consumer_io_wait_time_ns_avg gauge
kafka_consumer_io_wait_time_ns_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 1.656000219964789E8
# HELP kafka_consumer_coordinator_commit_latency_max The max time taken for a commit request
# TYPE kafka_consumer_coordinator_commit_latency_max gauge
kafka_consumer_coordinator_commit_latency_max{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 13.0
# HELP kafka_consumer_iotime_total *Deprecated* The total time the I/O thread spent doing I/O
# TYPE kafka_consumer_iotime_total counter
kafka_consumer_iotime_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 6.4699701E7
# HELP kafka_consumer_fetch_manager_records_consumed_total The total number of records consumed for a topic
# TYPE kafka_consumer_fetch_manager_records_consumed_total counter
kafka_consumer_fetch_manager_records_consumed_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",topic="app-subscriptions-2",} 1.0
# HELP kafka_consumer_node_request_rate The number of requests sent per second
# TYPE kafka_consumer_node_request_rate gauge
kafka_consumer_node_request_rate{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-1",spring_id="kafkaConsumerFactory.consumer-group-1",} 1.9713467048710602
kafka_consumer_node_request_rate{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node--1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
kafka_consumer_node_request_rate{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-2147483646",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.3637608593315359
# HELP kafka_consumer_commit_sync_time_ns_total The total time the consumer has spent in commitSync in nanoseconds
# TYPE kafka_consumer_commit_sync_time_ns_total counter
kafka_consumer_commit_sync_time_ns_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 1.5800001E7
# HELP kafka_consumer_coordinator_partition_lost_latency_max The max time taken for a partition-lost rebalance listener callback
# TYPE kafka_consumer_coordinator_partition_lost_latency_max gauge
kafka_consumer_coordinator_partition_lost_latency_max{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_coordinator_commit_total The total number of commit calls
# TYPE kafka_consumer_coordinator_commit_total counter
kafka_consumer_coordinator_commit_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 1.0
# HELP kafka_consumer_failed_authentication_rate The number of connections with failed authentication per second
# TYPE kafka_consumer_failed_authentication_rate gauge
kafka_consumer_failed_authentication_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_coordinator_failed_rebalance_rate_per_hour The number of failed rebalance events per hour
# TYPE kafka_consumer_coordinator_failed_rebalance_rate_per_hour gauge
kafka_consumer_coordinator_failed_rebalance_rate_per_hour{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_node_outgoing_byte_rate The number of outgoing bytes per second
# TYPE kafka_consumer_node_outgoing_byte_rate gauge
kafka_consumer_node_outgoing_byte_rate{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-1",spring_id="kafkaConsumerFactory.consumer-group-1",} 125.31517902168433
kafka_consumer_node_outgoing_byte_rate{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node--1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
kafka_consumer_node_outgoing_byte_rate{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-2147483646",spring_id="kafkaConsumerFactory.consumer-group-1",} 36.011554509468276
# HELP kafka_consumer_request_size_avg The average size of requests sent.
# TYPE kafka_consumer_request_size_avg gauge
kafka_consumer_request_size_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 69.06422018348624
# HELP kafka_consumer_node_incoming_byte_total The total number of incoming bytes
# TYPE kafka_consumer_node_incoming_byte_total counter
kafka_consumer_node_incoming_byte_total{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-1",spring_id="kafkaConsumerFactory.consumer-group-1",} 4125.0
kafka_consumer_node_incoming_byte_total{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node--1",spring_id="kafkaConsumerFactory.consumer-group-1",} 418.0
kafka_consumer_node_incoming_byte_total{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-2147483646",spring_id="kafkaConsumerFactory.consumer-group-1",} 1032.0
# HELP kafka_consumer_failed_authentication_total The total number of connections with failed authentication
# TYPE kafka_consumer_failed_authentication_total counter
kafka_consumer_failed_authentication_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_io_wait_time_ns_total The total time the I/O thread spent waiting
# TYPE kafka_consumer_io_wait_time_ns_total counter
kafka_consumer_io_wait_time_ns_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 7.605284052E10
# HELP kafka_consumer_connection_close_total The total number of connections closed
# TYPE kafka_consumer_connection_close_total counter
kafka_consumer_connection_close_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_incoming_byte_total The total number of bytes read off all sockets
# TYPE kafka_consumer_incoming_byte_total counter
kafka_consumer_incoming_byte_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 5575.0
# HELP kafka_consumer_request_total The total number of requests sent
# TYPE kafka_consumer_request_total counter
kafka_consumer_request_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 184.0
# HELP kafka_consumer_coordinator_join_total The total number of group joins
# TYPE kafka_consumer_coordinator_join_total counter
kafka_consumer_coordinator_join_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 1.0
# HELP kafka_consumer_fetch_manager_fetch_latency_avg The average time taken for a fetch request.
# TYPE kafka_consumer_fetch_manager_fetch_latency_avg gauge
kafka_consumer_fetch_manager_fetch_latency_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 506.8588235294118
# HELP kafka_consumer_coordinator_sync_time_avg The average time taken for a group sync
# TYPE kafka_consumer_coordinator_sync_time_avg gauge
kafka_consumer_coordinator_sync_time_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_node_outgoing_byte_total The total number of outgoing bytes
# TYPE kafka_consumer_node_outgoing_byte_total counter
kafka_consumer_node_outgoing_byte_total{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-1",spring_id="kafkaConsumerFactory.consumer-group-1",} 9451.0
kafka_consumer_node_outgoing_byte_total{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node--1",spring_id="kafkaConsumerFactory.consumer-group-1",} 180.0
kafka_consumer_node_outgoing_byte_total{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-2147483646",spring_id="kafkaConsumerFactory.consumer-group-1",} 3071.0
# HELP kafka_consumer_io_time_ns_avg The average length of time for I/O per select call in nanoseconds.
# TYPE kafka_consumer_io_time_ns_avg gauge
kafka_consumer_io_time_ns_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 93083.26056338029
# HELP kafka_consumer_coordinator_heartbeat_rate The number of heartbeats per second
# TYPE kafka_consumer_coordinator_heartbeat_rate gauge
kafka_consumer_coordinator_heartbeat_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.342392467365718
# HELP kafka_app_info_start_time_ms Metric indicating start-time-ms
# TYPE kafka_app_info_start_time_ms gauge
kafka_app_info_start_time_ms{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 1.658661281449E12
# HELP kafka_consumer_time_between_poll_avg The average delay between invocations of poll() in milliseconds.
# TYPE kafka_consumer_time_between_poll_avg gauge
kafka_consumer_time_between_poll_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 4651.636363636364
# HELP kafka_consumer_coordinator_partition_lost_latency_avg The average time taken for a partition-lost rebalance listener callback
# TYPE kafka_consumer_coordinator_partition_lost_latency_avg gauge
kafka_consumer_coordinator_partition_lost_latency_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_node_request_total The total number of requests sent
# TYPE kafka_consumer_node_request_total counter
kafka_consumer_node_request_total{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-1",spring_id="kafkaConsumerFactory.consumer-group-1",} 149.0
kafka_consumer_node_request_total{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node--1",spring_id="kafkaConsumerFactory.consumer-group-1",} 4.0
kafka_consumer_node_request_total{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-2147483646",spring_id="kafkaConsumerFactory.consumer-group-1",} 31.0
# HELP kafka_consumer_fetch_manager_records_consumed_rate The average number of records consumed per second for a topic
# TYPE kafka_consumer_fetch_manager_records_consumed_rate gauge
kafka_consumer_fetch_manager_records_consumed_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",topic="app-subscriptions-2",} 0.027109086965950987
# HELP kafka_consumer_coordinator_join_time_max The max time taken for a group rejoin
# TYPE kafka_consumer_coordinator_join_time_max gauge
kafka_consumer_coordinator_join_time_max{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_fetch_manager_records_lag_max The max lag of the partition
# TYPE kafka_consumer_fetch_manager_records_lag_max gauge
kafka_consumer_fetch_manager_records_lag_max{client_id="consumer-group-1",kafka_version="3.1.1",partition="0",spring_id="kafkaConsumerFactory.consumer-group-1",topic="app-subscriptions-2",} 0.0
# HELP kafka_consumer_fetch_manager_fetch_rate The number of fetch requests per second.
# TYPE kafka_consumer_fetch_manager_fetch_rate gauge
kafka_consumer_fetch_manager_fetch_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 1.9707325254727661
# HELP kafka_consumer_connection_creation_rate The number of new connections established per second
# TYPE kafka_consumer_connection_creation_rate gauge
kafka_consumer_connection_creation_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_response_rate The number of responses received per second
# TYPE kafka_consumer_response_rate gauge
kafka_consumer_response_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 2.3270928111700457
# HELP kafka_consumer_node_request_latency_avg
# TYPE kafka_consumer_node_request_latency_avg gauge
kafka_consumer_node_request_latency_avg{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
kafka_consumer_node_request_latency_avg{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node--1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
kafka_consumer_node_request_latency_avg{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-2147483646",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_io_ratio *Deprecated* The fraction of time the I/O thread spent doing I/O
# TYPE kafka_consumer_io_ratio gauge
kafka_consumer_io_ratio{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 5.576935880343305E-4
# HELP kafka_consumer_successful_reauthentication_rate The number of successful re-authentication of connections per second
# TYPE kafka_consumer_successful_reauthentication_rate gauge
kafka_consumer_successful_reauthentication_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_successful_reauthentication_total The total number of successful re-authentication of connections
# TYPE kafka_consumer_successful_reauthentication_total counter
kafka_consumer_successful_reauthentication_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_node_response_rate The number of responses received per second
# TYPE kafka_consumer_node_response_rate gauge
kafka_consumer_node_response_rate{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-1",spring_id="kafkaConsumerFactory.consumer-group-1",} 1.9710756113772319
kafka_consumer_node_response_rate{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node--1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
kafka_consumer_node_response_rate{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-2147483646",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.36377642728751175
# HELP kafka_consumer_incoming_byte_rate The number of bytes read off all sockets per second
# TYPE kafka_consumer_incoming_byte_rate gauge
kafka_consumer_incoming_byte_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 57.79995387743978
# HELP kafka_consumer_fetch_manager_records_lead The latest lead of the partition
# TYPE kafka_consumer_fetch_manager_records_lead gauge
kafka_consumer_fetch_manager_records_lead{client_id="consumer-group-1",kafka_version="3.1.1",partition="0",spring_id="kafkaConsumerFactory.consumer-group-1",topic="app-subscriptions-2",} 1.0
# HELP kafka_consumer_node_request_size_avg The average size of requests sent.
# TYPE kafka_consumer_node_request_size_avg gauge
kafka_consumer_node_request_size_avg{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-1",spring_id="kafkaConsumerFactory.consumer-group-1",} 63.56976744186046
kafka_consumer_node_request_size_avg{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node--1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
kafka_consumer_node_request_size_avg{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-2147483646",spring_id="kafkaConsumerFactory.consumer-group-1",} 99.0
# HELP kafka_consumer_network_io_total The total number of network operations (reads or writes) on all connections
# TYPE kafka_consumer_network_io_total counter
kafka_consumer_network_io_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 373.0
# HELP kafka_consumer_coordinator_join_time_avg The average time taken for a group rejoin
# TYPE kafka_consumer_coordinator_join_time_avg gauge
kafka_consumer_coordinator_join_time_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_response_total The total number of responses received
# TYPE kafka_consumer_response_total counter
kafka_consumer_response_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 183.0
# HELP kafka_consumer_fetch_manager_records_per_request_avg The average number of records in each request for a topic
# TYPE kafka_consumer_fetch_manager_records_per_request_avg gauge
kafka_consumer_fetch_manager_records_per_request_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",topic="app-subscriptions-2",} 1.0
# HELP kafka_consumer_connection_count_total The current number of active connections.
# TYPE kafka_consumer_connection_count_total counter
kafka_consumer_connection_count_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 3.0
# HELP kafka_consumer_coordinator_rebalance_total The total number of successful rebalance events, each event is composed of several failed re-trials until it succeeded
# TYPE kafka_consumer_coordinator_rebalance_total counter
kafka_consumer_coordinator_rebalance_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 1.0
# HELP kafka_consumer_coordinator_last_rebalance_seconds_ago The number of seconds since the last successful rebalance event
# TYPE kafka_consumer_coordinator_last_rebalance_seconds_ago gauge
kafka_consumer_coordinator_last_rebalance_seconds_ago{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 73.0
# HELP kafka_consumer_coordinator_failed_rebalance_total The total number of failed rebalance events
# TYPE kafka_consumer_coordinator_failed_rebalance_total counter
kafka_consumer_coordinator_failed_rebalance_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_io_time_ns_total The total time the I/O thread spent doing I/O
# TYPE kafka_consumer_io_time_ns_total counter
kafka_consumer_io_time_ns_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 6.4699701E7
# HELP kafka_consumer_fetch_manager_fetch_size_max The maximum number of bytes fetched per request for a topic
# TYPE kafka_consumer_fetch_manager_fetch_size_max gauge
kafka_consumer_fetch_manager_fetch_size_max{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",topic="app-subscriptions-2",} 294.0
# HELP kafka_consumer_coordinator_sync_rate The number of group syncs per second
# TYPE kafka_consumer_coordinator_sync_rate gauge
kafka_consumer_coordinator_sync_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_coordinator_heartbeat_total The total number of heartbeats
# TYPE kafka_consumer_coordinator_heartbeat_total counter
kafka_consumer_coordinator_heartbeat_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 25.0
# HELP kafka_consumer_io_waittime_total *Deprecated* The total time the I/O thread spent waiting
# TYPE kafka_consumer_io_waittime_total counter
kafka_consumer_io_waittime_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 7.605284052E10
# HELP kafka_consumer_successful_authentication_rate The number of connections with successful authentication per second
# TYPE kafka_consumer_successful_authentication_rate gauge
kafka_consumer_successful_authentication_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_node_request_size_max The maximum size of any request sent.
# TYPE kafka_consumer_node_request_size_max gauge
kafka_consumer_node_request_size_max{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-1",spring_id="kafkaConsumerFactory.consumer-group-1",} 112.0
kafka_consumer_node_request_size_max{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node--1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
kafka_consumer_node_request_size_max{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-2147483646",spring_id="kafkaConsumerFactory.consumer-group-1",} 147.0
# HELP kafka_consumer_network_io_rate The number of network operations (reads or writes) on all connections per second
# TYPE kafka_consumer_network_io_rate gauge
kafka_consumer_network_io_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 4.737945492662473
# HELP kafka_consumer_request_size_max The maximum size of any request sent.
# TYPE kafka_consumer_request_size_max gauge
kafka_consumer_request_size_max{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 147.0
# HELP kafka_consumer_reauthentication_latency_avg The average latency observed due to re-authentication
# TYPE kafka_consumer_reauthentication_latency_avg gauge
kafka_consumer_reauthentication_latency_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_coordinator_sync_time_max The max time taken for a group sync
# TYPE kafka_consumer_coordinator_sync_time_max gauge
kafka_consumer_coordinator_sync_time_max{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_node_request_latency_max
# TYPE kafka_consumer_node_request_latency_max gauge
kafka_consumer_node_request_latency_max{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
kafka_consumer_node_request_latency_max{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node--1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
kafka_consumer_node_request_latency_max{client_id="consumer-group-1",kafka_version="3.1.1",node_id="node-2147483646",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_coordinator_last_heartbeat_seconds_ago The number of seconds since the last coordinator heartbeat was sent
# TYPE kafka_consumer_coordinator_last_heartbeat_seconds_ago gauge
kafka_consumer_coordinator_last_heartbeat_seconds_ago{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 1.0
# HELP kafka_consumer_request_rate The number of requests sent per second
# TYPE kafka_consumer_request_rate gauge
kafka_consumer_request_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 2.3270440251572326
# HELP kafka_consumer_successful_authentication_no_reauth_total The total number of connections with successful authentication where the client does not support re-authentication
# TYPE kafka_consumer_successful_authentication_no_reauth_total counter
kafka_consumer_successful_authentication_no_reauth_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_fetch_manager_records_lead_min The min lead of the partition
# TYPE kafka_consumer_fetch_manager_records_lead_min gauge
kafka_consumer_fetch_manager_records_lead_min{client_id="consumer-group-1",kafka_version="3.1.1",partition="0",spring_id="kafkaConsumerFactory.consumer-group-1",topic="app-subscriptions-2",} 1.0
# HELP kafka_consumer_fetch_manager_bytes_consumed_total The total number of bytes consumed for a topic
# TYPE kafka_consumer_fetch_manager_bytes_consumed_total counter
kafka_consumer_fetch_manager_bytes_consumed_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",topic="app-subscriptions-2",} 294.0
# HELP kafka_consumer_coordinator_partition_revoked_latency_max The max time taken for a partition-revoked rebalance listener callback
# TYPE kafka_consumer_coordinator_partition_revoked_latency_max gauge
kafka_consumer_coordinator_partition_revoked_latency_max{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_fetch_manager_preferred_read_replica The current read replica for the partition, or -1 if reading from leader
# TYPE kafka_consumer_fetch_manager_preferred_read_replica gauge
kafka_consumer_fetch_manager_preferred_read_replica{client_id="consumer-group-1",kafka_version="3.1.1",partition="0",spring_id="kafkaConsumerFactory.consumer-group-1",topic="app-subscriptions-2",} -1.0
# HELP kafka_consumer_coordinator_rebalance_rate_per_hour The number of successful rebalance events per hour, each event is composed of several failed re-trials until it succeeded
# TYPE kafka_consumer_coordinator_rebalance_rate_per_hour gauge
kafka_consumer_coordinator_rebalance_rate_per_hour{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_coordinator_join_rate The number of group joins per second
# TYPE kafka_consumer_coordinator_join_rate gauge
kafka_consumer_coordinator_join_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_poll_idle_ratio_avg The average fraction of time the consumer's poll() is idle as opposed to waiting for the user code to process records.
# TYPE kafka_consumer_poll_idle_ratio_avg gauge
kafka_consumer_poll_idle_ratio_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.4984942694187732
# HELP kafka_consumer_fetch_manager_fetch_latency_max The max time taken for any fetch request.
# TYPE kafka_consumer_fetch_manager_fetch_latency_max gauge
kafka_consumer_fetch_manager_fetch_latency_max{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 570.0
# HELP kafka_consumer_successful_authentication_total The total number of connections with successful authentication
# TYPE kafka_consumer_successful_authentication_total counter
kafka_consumer_successful_authentication_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_coordinator_assigned_partitions The number of partitions currently assigned to this consumer
# TYPE kafka_consumer_coordinator_assigned_partitions gauge
kafka_consumer_coordinator_assigned_partitions{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 1.0
# HELP kafka_consumer_connection_close_rate The number of connections closed per second
# TYPE kafka_consumer_connection_close_rate gauge
kafka_consumer_connection_close_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_coordinator_commit_rate The number of commit calls per second
# TYPE kafka_consumer_coordinator_commit_rate gauge
kafka_consumer_coordinator_commit_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.027139988058405257
# HELP kafka_consumer_coordinator_rebalance_latency_total The total number of milliseconds this consumer has spent in successful rebalances since creation
# TYPE kafka_consumer_coordinator_rebalance_latency_total counter
kafka_consumer_coordinator_rebalance_latency_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 3014.0
# HELP kafka_consumer_coordinator_rebalance_latency_avg The average time taken for a group to complete a successful rebalance, which may be composed of several failed re-trials until it succeeded
# TYPE kafka_consumer_coordinator_rebalance_latency_avg gauge
kafka_consumer_coordinator_rebalance_latency_avg{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} NaN
# HELP kafka_consumer_fetch_manager_records_lag_avg The average lag of the partition
# TYPE kafka_consumer_fetch_manager_records_lag_avg gauge
kafka_consumer_fetch_manager_records_lag_avg{client_id="consumer-group-1",kafka_version="3.1.1",partition="0",spring_id="kafkaConsumerFactory.consumer-group-1",topic="app-subscriptions-2",} 0.0
# HELP kafka_consumer_fetch_manager_records_lead_avg The average lead of the partition
# TYPE kafka_consumer_fetch_manager_records_lead_avg gauge
kafka_consumer_fetch_manager_records_lead_avg{client_id="consumer-group-1",kafka_version="3.1.1",partition="0",spring_id="kafkaConsumerFactory.consumer-group-1",topic="app-subscriptions-2",} 1.0
# HELP kafka_consumer_time_between_poll_max The max delay between invocations of poll() in milliseconds.
# TYPE kafka_consumer_time_between_poll_max gauge
kafka_consumer_time_between_poll_max{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 5002.0
# HELP kafka_consumer_select_rate The number of times the I/O layer checked for new I/O to perform per second
# TYPE kafka_consumer_select_rate gauge
kafka_consumer_select_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 5.978294832003666
# HELP kafka_consumer_coordinator_heartbeat_response_time_max The max time taken to receive a response to a heartbeat request
# TYPE kafka_consumer_coordinator_heartbeat_response_time_max gauge
kafka_consumer_coordinator_heartbeat_response_time_max{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 38.0
# HELP kafka_consumer_committed_time_ns_total The total time the consumer has spent in committed in nanoseconds
# TYPE kafka_consumer_committed_time_ns_total counter
kafka_consumer_committed_time_ns_total{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
# HELP kafka_consumer_failed_reauthentication_rate The number of failed re-authentication of connections per second
# TYPE kafka_consumer_failed_reauthentication_rate gauge
kafka_consumer_failed_reauthentication_rate{client_id="consumer-group-1",kafka_version="3.1.1",spring_id="kafkaConsumerFactory.consumer-group-1",} 0.0
```
