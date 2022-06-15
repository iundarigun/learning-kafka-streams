package br.com.devcave.kafkastream.controller

import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.springframework.http.ResponseEntity
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("balance")
class BalanceController(
    private val factoryBean: StreamsBuilderFactoryBean
) {

    @GetMapping("{account}")
    fun getAccountBalance(@PathVariable account: String): ResponseEntity<Long> {
        val balances = factoryBean.kafkaStreams?.store<ReadOnlyKeyValueStore<String, Long>>(
            StoreQueryParameters.fromNameAndType(
                "balance",
                QueryableStoreTypes.keyValueStore()
            )
        )
        return balances?.get(account)?.let { ResponseEntity.ok(it) }?: ResponseEntity.notFound().build()

    }
}