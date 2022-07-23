package publisher

import com.github.frtu.sample.basic.persistence.basic.AccountSubscriptionEntity
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.kafka.sender.SenderOptions

@Service
class EventSink(
    @Value(value = "\${application.broker.topics.subscriptions}")
    val outputSource: String,
    senderOptions: SenderOptions<String, AccountSubscriptionEntity>,
) {
    val reactiveKafkaProducerTemplate = ReactiveKafkaProducerTemplate(senderOptions)

    suspend fun emit(event: AccountSubscriptionEntity) =
        reactiveKafkaProducerTemplate.send(outputSource, event).awaitFirstOrNull()
}