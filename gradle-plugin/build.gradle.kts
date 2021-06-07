plugins {
    kotlin
    detekt
    publish
    id("java-gradle-plugin")
}

gradlePlugin {
    plugins {
        register("ejson") {
            id = "com.eygraber.ejson"
            implementationClass = "com.eygraber.ejson.gradle.EjsonPlugin"
        }
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    compileOnly("com.android.tools.build:gradle:7.0.0-beta03")
}
