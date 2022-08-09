import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
}

tasks.withType<JavaCompile> {
  sourceCompatibility = libs.versions.jdk.get()
  targetCompatibility = libs.versions.jdk.get()
}

kotlin {
  jvmToolchain {
    require(this is JavaToolchainSpec)
    languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
    vendor.set(JvmVendorSpec.AZUL)
  }
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = libs.versions.jdk.get()
  }
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.buildscript.detekt)
  implementation(libs.buildscript.dokka)
  implementation(libs.buildscript.kotlin)
  implementation(libs.buildscript.publish)

  implementation(libs.buildscript.utils.kotlin)
  implementation(libs.buildscript.utils.detekt)
}
