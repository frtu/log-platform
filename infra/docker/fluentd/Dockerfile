# https://hub.docker.com/r/fluent/fluentd/
FROM fluent/fluentd:v1.7-debian-1

ARG FLUENTD_OPT

ARG FLUENTD_CONF=fluent.conf
ENV FLUENTD_CONF=$FLUENTD_CONF

# Use root account to use apt
USER root

# Do not split this into multiple RUN!
# Docker creates a layer for every RUN-Statement
# therefore an 'apk delete build*' has no effect
RUN buildDeps="sudo make gcc g++ libc-dev curl git" \
 && apt-get update \
 && apt-get install -y --no-install-recommends $buildDeps \
 && sudo gem install fluent-plugin-elasticsearch \
 && sudo gem install fluent-plugin-prometheus --version='~>1.6.1' \
 && sudo gem install fluent-plugin-flowcounter -v 0.4.2 \
 && sudo gem sources --clear-all \
 && SUDO_FORCE_REMOVE=yes \
    apt-get purge -y --auto-remove \
                  -o APT::AutoRemove::RecommendsImportant=false \
                  $buildDeps \
 && rm -rf /var/lib/apt/lists/* \
 && rm -rf /tmp/* /var/tmp/* /usr/lib/ruby/gems/*/cache/*.gem

COPY conf/$FLUENTD_CONF /fluentd/etc/fluent.conf

USER fluent
EXPOSE 24220 24224

CMD fluentd -c /fluentd/etc/fluent.conf -p /fluentd/plugins $FLUENTD_OPT
