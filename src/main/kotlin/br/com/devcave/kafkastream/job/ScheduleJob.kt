package br.com.devcave.kafkastream.job

import br.com.devcave.kafkastream.event.PaymentEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduleJob(
    private val kafkaTemplate: KafkaTemplate<String, PaymentEvent>,
    @Value("\${spring.kafka.consumer.topics.payment}")
    private val paymentTopic: String
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedRate = 10_000)
    fun job() {
        logger.info("Executing job ...")
        (0..10).forEach {
            val payment = PaymentEvent.randomPayment()
            logger.info("putting on topic $paymentTopic payment $payment")
            Thread.sleep(500)
            kafkaTemplate.send(paymentTopic, payment.paymentId, payment)
        }
    }
}