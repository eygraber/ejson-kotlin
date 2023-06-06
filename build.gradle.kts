import com.eygraber.conventions.tasks.deleteRootBuildDirWhenCleaning
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

buildscript {
  dependencies {
    classpath(libs.buildscript.detekt)
    classpath(libs.buildscript.dokka)
    classpath(libs.buildscript.kotlin)
    classpath(libs.buildscript.publish)
  }
}

plugins {
  base
  alias(libs.plugins.conventions)
}

project.deleteRootBuildDirWhenCleaning()

gradleConventionsDefaults {
  detekt {
    plugins(libs.detektEygraber.formatting)
    plugins(libs.detektEygraber.style)
  }

  kotlin {
    jdkVersion = libs.versions.jdk.get()
    jvmDistribution = JvmVendorSpec.AZUL
    explicitApiMode = ExplicitApiMode.Strict
  }
}
