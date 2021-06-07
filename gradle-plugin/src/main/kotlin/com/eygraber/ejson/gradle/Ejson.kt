package com.eygraber.ejson.gradle

import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import java.io.ByteArrayOutputStream
import java.io.File

class Ejson(
  private val project: Project,
  private val extension: EjsonExtension
) {
  @Suppress("UNCHECKED_CAST")
  private fun String.toMap() = JsonSlurper().parseText(this) as MutableMap<String, Any>

  fun decrypt(secrets: File): MutableMap<String, Any> {
    if(!secrets.exists()) {
      project.logger.log(
        LogLevel.INFO,
        "Ejson: Didn't find a secrets file at $secrets...ignoring"
      )
      return HashMap(0)
    }

    val keydir = File(System.getenv("EJSON_KEYDIR") ?: "/opt/ejson/keys")

    val stdout = ByteArrayOutputStream()
    val stderr = ByteArrayOutputStream()

    val execResult = project.exec { execSpec ->
      with(execSpec) {
        commandLine("ejson", "--keydir", keydir.absolutePath, "decrypt", secrets.absolutePath)
        isIgnoreExitValue = true
        standardOutput = stdout
        errorOutput = stderr
      }
    }

    return if(execResult.exitValue != 0) {
      val err = if (stdout.size() == 0) stderr else stdout
      throw GradleException("ejson exited with a non-zero value (${execResult.exitValue}) - $err")
    } else {
      stdout
        .toString()
        .toMap()
    }
  }
}
