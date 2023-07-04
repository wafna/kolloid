rootProject.name = "kjs"

pluginManagement {
    val kotlinVersion: String by settings
    val dokkaVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion apply false
        kotlin("js") version kotlinVersion apply false
        kotlin("plugin.serialization") version kotlinVersion apply false
        id("org.jetbrains.dokka") version dokkaVersion apply false
    }
}

include(
    "util",
    "domain",
    "db",
    "server",
    "client",
    "browser",
    "test",
    "integration-test"
)
