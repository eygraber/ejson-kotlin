package com.eygraber.ejson.gradle

import com.eygraber.ejson.Ejson
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask
public abstract class EjsonDecryptTask : DefaultTask() {
  @get:InputFile
  @get:PathSensitive(PathSensitivity.RELATIVE)
  public abstract val secretsFile: RegularFileProperty

  @get:Optional
  @get:Input
  public abstract val userSuppliedPrivateKey: Property<String>

  @get:Optional
  @get:Input
  public abstract val outputKey: Property<String>

  @get:Optional
  @get:Input
  public abstract val outputFileName: Property<String>

  @get:OutputDirectory
  public abstract val output: DirectoryProperty

  @TaskAction
  public fun decrypt() {
    val transformOutput = { json: JsonObject ->
      outputKey.orNull?.let { key ->
        when(val field = json[key]) {
          is JsonPrimitive -> requireNotNull(field.contentOrNull)
          else -> field.toString()
        }
      } ?: json.toString()
    }

    val outputText = Ejson().decryptSecrets(
      secretsFile = secretsFile.get().asFile.toPath(),
      userSuppliedPrivateKey = userSuppliedPrivateKey.orNull,
      transform = transformOutput
    )

    File(output.get().asFile, outputFileName.getOrElse("ejson")).writeText(outputText)
  }
}
