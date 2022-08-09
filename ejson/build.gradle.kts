plugins {
  kotlin
  detekt
  publish
}


repositories {
  mavenCentral()
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
