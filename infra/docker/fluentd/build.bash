IMAGE_NAME=${1:-fluentd-es:latest}
FLUENTD_CONF=${2:-fluent.conf}
FLUENTD_OPT=${@:3}

docker build --force-rm=true --no-cache=true -t ${IMAGE_NAME} --build-arg FLUENTD_CONF=${FLUENTD_CONF} --build-arg FLUENTD_OPT=${FLUENTD_OPT} .
