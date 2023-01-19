plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

dependencies {
  implementation(libs.tweetnacl)

  testRuntimeOnly(libs.test.junit.engine)
  testImplementation(libs.test.junit.api)
  testImplementation(libs.test.junit.pioneer)
  testImplementation(libs.test.junit.extensions)
  testImplementation(libs.test.assertk)
  testImplementation(libs.kotlinx.serialization.json)
  testImplementation(libs.test.jimfs)
}

tasks.test {
  useJUnitPlatform()
}
