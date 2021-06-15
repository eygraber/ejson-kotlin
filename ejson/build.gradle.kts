plugins {
    kotlin
    detekt
    publish
}


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.purejava:tweetnacl-java:1.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
}
