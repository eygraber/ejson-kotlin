@file:OptIn(ExperimentalSerializationApi::class)

package com.eygraber.ejson

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

public class Ejson(
  private val prettyPrintOutput: Boolean = Json.Default.configuration.prettyPrint,
  private val prettyPrintIndentSize: Int = Json.Default.configuration.prettyPrintIndent.length,
  overrideKeyDir: File? = null
) {
  private val keyDir by lazy {
    overrideKeyDir
      ?: File(System.getenv("EJSON_KEYDIR")).takeIf(File::exists)
      ?: File("/opt/ejson/keys").takeIf(File::exists)
  }

  private val json = Json {
    prettyPrint = prettyPrintOutput
    prettyPrintIndent = " ".repeat(prettyPrintIndentSize)
  }

  public fun encrypt(secretsJsonString: String): Result {
    val (secrets, jsonPublicKey) = when(val jsonPublicKey = secretsJsonString.parseSecrets()) {
      is SecretFile.Error -> error("Encryption failed: ${jsonPublicKey.error}")

      is SecretFile.Success -> jsonPublicKey
    }

    val encrypter = Encrypter(jsonPublicKey)
    return runCatching {
      Result.Success(json.encodeToString(secrets.walkAndTransformStrings(encrypter::encrypt)))
    }.getOrElse {
      Result.Error("Encryption failed: ${it.message ?: "no message"}")
    }
  }

  public fun encrypt(ejsonSecretsFile: File): Result {
    val secretsJsonString = try {
      ejsonSecretsFile.readText()
    }
    catch(t: Throwable) {
      error("Encryption failed: An error occurred while reading ${ejsonSecretsFile.absolutePath} - ${t.message}")
    }

    return encrypt(secretsJsonString)
  }

  public fun decrypt(secretsJsonString: String): Result {
    val (secrets, jsonPublicKey) = when(val jsonPublicKey = secretsJsonString.parseSecrets()) {
      is SecretFile.Error -> error("Decryption failed: ${jsonPublicKey.error}")

      is SecretFile.Success -> jsonPublicKey
    }

    if(keyDir == null) error("Decryption failed: couldn't read key file $jsonPublicKey")

    val privateKeyString = try {
      File(keyDir, jsonPublicKey.key.encodeHex()).readText().trim()
    }
    catch(t: Throwable) {
      error("Decryption failed: couldn't read key file $jsonPublicKey (${t.message})")
    }

    val privateKey = privateKeyString.toPrivateKey().getOrThrow()

    val decrypter = Decrypter(privateKey)
    return runCatching {
      Result.Success(json.encodeToString(secrets.walkAndTransformStrings(decrypter::decrypt)))
    }.getOrElse {
      Result.Error("Decryption failed: ${it.message ?: "no message"}")
    }
  }

  public fun decrypt(ejsonSecretsFile: File): Result {
    val secretsJsonString = try {
      ejsonSecretsFile.readText()
    }
    catch(t: Throwable) {
      error("Decryption failed: An error occurred while reading ${ejsonSecretsFile.absolutePath} - ${t.message}")
    }

    return decrypt(secretsJsonString)
  }

  public sealed class Result {
    public data class Error(val error: String) : Result()

    public data class Success(val json: String) : Result()
  }

  private fun String.parseSecrets(): SecretFile {
    val topLevelElement = try {
      json.parseToJsonElement(this)
    }
    catch(t: Throwable) {
      return SecretFile.Error("An error occurred while parsing json - ${t.message}")
    }

    val secretsJson = topLevelElement as? JsonObject
      ?: return SecretFile.Error("expected a JsonObject but received ${topLevelElement.javaClass.simpleName}")

    val primitive = secretsJson["_public_key"]
      ?: return SecretFile.Error("public key not present in EJSON file")

    val keyString = primitive.runCatching {
      jsonPrimitive.contentOrNull
    }.getOrNull() ?: return SecretFile.Error("public key has invalid format")

    return runCatching {
      SecretFile.Success(secretsJson, keyString.toPublicKey().getOrThrow())
    }.getOrElse {
      SecretFile.Error(it.message ?: "no message")
    }
  }
}

private sealed class SecretFile {
  data class Error(val error: String) : SecretFile()

  data class Success(val secretsJson: JsonObject, val key: PublicKey) : SecretFile()
}
