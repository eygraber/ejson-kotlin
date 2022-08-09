plugins {
  kotlin
  detekt
  publish
}

repositories {
  mavenCentral()
}

dependencies {
  compileOnly(gradleApi())
  api(project(":ejson"))
  compileOnly(libs.kotlinx.serialization.json)
}
