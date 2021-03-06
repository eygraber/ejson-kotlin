plugins {
  `kotlin-dsl`
}

repositories {
  google()
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
  implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.18.1")
  implementation("com.vanniktech:gradle-maven-publish-plugin:0.18.0")
  implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.5.31")
}
