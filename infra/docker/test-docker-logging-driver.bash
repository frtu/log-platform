# https://www.fluentd.org/guides/recipes/docker-logging
# docker run --log-driver=fluentd --log-opt fluentd-address=localhost:24224 ubuntu echo 'Hello Fluentd!'
docker run --rm --name test-docker-fluent-logger --log-driver=fluentd --log-opt tag=docker.{{.ID}} ubuntu echo "{\"key1\":\"value2\"}"