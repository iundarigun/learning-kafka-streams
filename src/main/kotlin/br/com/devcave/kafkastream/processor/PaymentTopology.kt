package br.com.devcave.kafkastream.processor

import br.com.devcave.kafkastream.event.PaymentEvent
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Grouped
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.stereotype.Component


@Component
class PaymentTopology(
    @Value("\${spring.kafka.consumer.topics.payment}")
    private val paymentTopic: String,
    @Value("\${spring.kafka.producer.topics.outbound}")
    private val outboundTopic: String,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    fun buildPipeline(streamsBuilder: StreamsBuilder) {
        val messageStream = streamsBuilder.stream(
            paymentTopic,
            Consumed.with(Serdes.String(), serdes)
        ).peek { key, payment ->
            logger.info("streaming key $key and payment $payment")
        }.filter{ _, payment ->
            payment.currency != "GBP"
        }

        val branch = messageStream.split(Named.`as`("CURRENCY")).branch { _, payment ->
            payment.currency == "EUR"
        }.branch { _, payment ->
            payment.currency == "USD"
        }.noDefaultBranch()

        val transform = branch["CURRENCY2"]?.mapValues { _, payment ->
            payment.copy(currency = "EUR", amount = (payment.amount * 0.89).toLong())
        }

        val mergedStream = branch["CURRENCY1"]?.merge(transform)

        mergedStream
            ?.map{ _, payment -> KeyValue(payment.fromAccount, payment.amount)}
            ?.groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
            ?.aggregate(
                { 0L },
                { _, value, aggregate -> aggregate + value },
                Materialized.`as`<String?, Long?, KeyValueStore<Bytes, ByteArray>?>("balance").withKeySerde(Serdes.String()).withValueSerde(Serdes.Long()))

            mergedStream?.to(outboundTopic, Produced.with(Serdes.String(), serdes))

        logger.info("Finalizing")
    }

    companion object {
        val serdes = Serdes.serdeFrom(
            JsonSerializer(),
            JsonDeserializer(PaymentEvent::class.java)
        )

    }
}