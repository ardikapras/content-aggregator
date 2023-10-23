package com.ardikapras

import com.ardikapras.config.DatabaseConfig
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    DatabaseConfig.init()
}
