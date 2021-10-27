import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of("11"))
  }
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
  kotlinOptions.jvmTarget = "11"
}
