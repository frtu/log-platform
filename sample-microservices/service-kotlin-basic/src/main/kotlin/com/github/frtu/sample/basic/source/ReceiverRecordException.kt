package com.github.frtu.sample.basic.source

import reactor.kafka.receiver.ReceiverRecord

class ReceiverRecordException(
    val record: ReceiverRecord<*, *>,
    t: Throwable?
) : RuntimeException(t)