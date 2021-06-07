plugins {
  `kotlin-dsl`
}

repositories {
  google()
  mavenCentral()
}

dependencies {
  implementation(kotlin("gradle-plugin", "1.5.10"))
  implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.17.1")
  implementation("com.vanniktech:gradle-maven-publish-plugin:0.15.1")
  implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.4.32")
}
