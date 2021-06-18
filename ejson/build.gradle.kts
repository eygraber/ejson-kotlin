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

  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
  testImplementation("org.junit-pioneer:junit-pioneer:1.4.2")
  testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.24")
  testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
  testImplementation("com.google.jimfs:jimfs:1.2")
}

tasks.test {
  useJUnitPlatform()
}

kotlin {
  explicitApi()
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}
