plugins {
  kotlin("jvm") version ("1.4.31")
  detekt
  publish
}

repositories {
  mavenCentral()
}

dependencies {
  compileOnly(gradleApi())
  implementation(project(":ejson"))
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
}

kotlin {
  explicitApi()
}
