package br.com.devcave.kafkastream.configuration

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration

@Configuration
@EnableKafkaStreams
class KafkaConfiguration {

    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
    fun kafkaStreamsConfig(kafkaProperties: KafkaProperties): KafkaStreamsConfiguration {
        val props = mapOf(
            Pair(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams"),
            Pair(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers),
            Pair(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().javaClass.name),
            Pair(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().javaClass.name),
            Pair(StreamsConfig.STATE_DIR_CONFIG, "./rockdb")
        )
        return KafkaStreamsConfiguration(props)
    }
}