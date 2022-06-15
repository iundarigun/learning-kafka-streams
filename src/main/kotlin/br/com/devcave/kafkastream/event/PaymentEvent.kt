package br.com.devcave.kafkastream.event

import java.util.UUID
import kotlin.random.Random

data class PaymentEvent(
    val paymentId: String,
    val amount: Long,
    val currency: String,
    val toAccount: String,
    val fromAccount: String,
    val rails:String
) {
    companion object {
        private val CURRENCY = listOf("EUR", "USD", "GBP")
        fun randomPayment(): PaymentEvent {
            val random = Random(System.currentTimeMillis())
            return PaymentEvent(
                paymentId = UUID.randomUUID().toString(),
                amount = random.nextLong(100, 100_000),
                currency =  CURRENCY[random.nextInt(0,3)],
                toAccount = random.nextInt(1, 100).toString(),
                fromAccount = random.nextInt(1, 100).toString(),
                rails = "XPTO"
            )
        }
    }
}
