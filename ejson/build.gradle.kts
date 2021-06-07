plugins {
    kotlin
    detekt
    publish
}


repositories {
    mavenCentral()
}

dependencies {
    implementation("software.pando.crypto:salty-coffee:1.0.3")
}
