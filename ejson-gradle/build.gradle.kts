plugins {
  kotlin("jvm")
  detekt
  publish
}

repositories {
  mavenCentral()
}

dependencies {
  compileOnly(gradleApi())
  api(project(":ejson"))
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
}

kotlin {
  explicitApi()
}
