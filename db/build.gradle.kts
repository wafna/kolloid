plugins {
    id("kotlin-jvm-library")
    id("org.jetbrains.dokka")
}

// Version should match kotlin version in plugins, above.
val kotlinVersion: String by project

dependencies {
    api("com.zaxxer:HikariCP:5.0.1")
    api("org.postgresql:postgresql:42.6.0")
    api("org.flywaydb:flyway-core:9.18.0")
    // DB
    api("org.ktorm:ktorm-core:3.6.0")

    implementation(project(":util"))
    api(project(":domain"))
}
