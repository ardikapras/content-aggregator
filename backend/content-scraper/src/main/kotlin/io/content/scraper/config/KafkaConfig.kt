package io.content.scraper.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.Properties

@Configuration
class KafkaConfig {
    private val bootstrapServer = "localhost:9092"
    private val groupId = "content-scraper"
    private val keySerializer = "org.apache.kafka.common.serialization.StringSerializer"
    private val valueSerializer = "org.apache.kafka.common.serialization.StringSerializer"
    private val keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer"
    private val valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer"

    @Bean
    fun producerProperties(): Properties =
        Properties().apply {
            put("bootstrap.servers", bootstrapServer)
            put("key.serializer", keySerializer)
            put("value.serializer", valueSerializer)
        }

    @Bean
    fun consumerProperties(): Properties =
        Properties().apply {
            put("bootstrap.servers", bootstrapServer)
            put("group.id", groupId)
            put("key.deserializer", keyDeserializer)
            put("value.deserializer", valueDeserializer)
            put("auto.offset.reset", "earliest")
        }
}
