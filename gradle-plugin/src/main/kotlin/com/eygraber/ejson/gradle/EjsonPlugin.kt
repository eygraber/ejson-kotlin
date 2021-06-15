package com.eygraber.ejson.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ExtraPropertiesExtension
import java.io.File

private data class EjsonError(
  val variantName: String,
  val throwable: Throwable
)

public class EjsonPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val ejsonExtension = project.extensions.create("ejson", EjsonExtension::class.java)

    val ejson = Ejson(project, ejsonExtension)

    val globalSecrets = ejson.decrypt(secrets = project.file("secrets.ejson"))

    project.logger.log(
      LogLevel.DEBUG,
      "Ejson: Global Secrets - $globalSecrets"
    )

    var error: EjsonError? = null

    globalSecrets.remove("_public_key")

    val variantSecrets = mutableMapOf<String, MutableMap<String, Any>>()

    val dirsWithSecrets =
      project
        .file("src")
        .takeIf { it.exists() }
        ?.listFiles { f ->
          f.isDirectory && f.listFiles().find { child ->
            child.name == "secrets.ejson"
          } != null
        }
        ?: emptyArray()

    dirsWithSecrets
      .forEach { dir ->
        val name = dir.name

        project.logger.log(
          LogLevel.INFO,
          "Ejson: processing android source - $name"
        )

        try {
          ejson.decrypt(
            secrets = File(dir, "secrets.ejson")
          ).takeIf { it.isNotEmpty() }?.let {
            it.remove("_public_key")
            variantSecrets[name] = it
            project.logger.log(
              LogLevel.DEBUG,
              "Ejson: $name Secrets - $it"
            )
          }
        }
        catch(e: Throwable) {
          if (error == null) error = EjsonError(name, e)
        }
      }

    project.extensions.findByType(ExtraPropertiesExtension::class.java)?.let { extra ->
      extra.set("ejsonGlobalSecrets", globalSecrets)
      extra.set("ejsonVariantSecrets", variantSecrets)
    }

    project.afterEvaluate {
      error?.apply {
        if(ejsonExtension.onEjsonFailure(variantName)) {
          throw throwable
        }
      } ?: ejsonExtension.onSecretsDecrypted()
    }
  }
}
