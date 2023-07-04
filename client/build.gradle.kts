plugins {
    id("kotlin-jvm-application")
    id("org.jetbrains.dokka")
}

val kotlinVersion: String by project
val arrowVersion: String by project
val ktorVersion: String by project

dependencies {
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    implementation(project(":util"))
    implementation(project(":domain"))
}

application {
    mainClass.set("wafna.kolloid.client.ClientKt")
}
