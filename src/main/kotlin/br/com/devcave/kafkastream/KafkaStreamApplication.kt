package br.com.devcave.kafkastream

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class KafkaStreamApplication

fun main(args: Array<String>) {
	runApplication<KafkaStreamApplication>(*args)
}
