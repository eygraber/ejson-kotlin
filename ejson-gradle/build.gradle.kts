plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-detekt2")
  id("com.eygraber.conventions-publish-maven-central")
}

dependencies {
  compileOnly(gradleApi())
  api(project(":ejson"))
  compileOnly(libs.kotlinx.serialization.json)
}
