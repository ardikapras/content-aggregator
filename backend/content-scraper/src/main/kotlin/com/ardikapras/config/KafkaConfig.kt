package com.ardikapras.config

import java.util.*

object KafkaConfig {

    private const val BOOTSTRAP_SERVERS = "localhost:9092"
    private const val KEY_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer"
    private const val VALUE_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer"

    val producerProps: Properties
        get() = Properties().apply {
            put("bootstrap.servers", BOOTSTRAP_SERVERS)
            put("key.serializer", KEY_SERIALIZER)
            put("value.serializer", VALUE_SERIALIZER)
        }
}
