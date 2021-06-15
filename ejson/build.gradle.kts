import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version ("1.5.10")
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

kotlin {
  explicitApi()
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}
