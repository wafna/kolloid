package wafna.kolloid.server

import wafna.kolloid.db.DatabaseConfig

data class ServerConfig(val host: String, val port: Int, val static: String)

data class AppConfig(val env: String, val database: DatabaseConfig, val server: ServerConfig)
