package com.ardikapras.config

import java.util.*

object KafkaConfig {

    private const val BOOTSTRAP_SERVERS = "localhost:9092"
    private const val GROUP_ID = "content-scraper"
    private const val KEY_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer"
    private const val VALUE_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer"
    private const val KEY_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer"
    private const val VALUE_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer"

    val producerProps: Properties
        get() = Properties().apply {
            put("bootstrap.servers", BOOTSTRAP_SERVERS)
            put("key.serializer", KEY_SERIALIZER)
            put("value.serializer", VALUE_SERIALIZER)
        }

    val consumerProps: Properties
        get() = Properties().apply {
            put("bootstrap.servers", BOOTSTRAP_SERVERS)
            put("group.id", GROUP_ID)
            put("key.deserializer", KEY_DESERIALIZER)
            put("value.deserializer", VALUE_DESERIALIZER)
            put("auto.offset.reset", "earliest")
        }
}
