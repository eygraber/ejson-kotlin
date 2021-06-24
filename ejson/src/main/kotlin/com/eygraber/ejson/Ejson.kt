package com.eygraber.ejson

import java.nio.file.FileSystems
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.io.path.readText

private fun defaultfs() =
  try {
    Class.forName("java.nio.file.FileSystems")
    FileSystems.getDefault()
  }
  catch(_: Throwable) {
    Any()
  }

public class Ejson(
  overrideKeyDir: Path? = null,
  filesystem: Any = defaultfs()
) {
  private val keyDirProvider = KeyDirProvider(
    filesystem,
    overrideKeyDir
  )

  public fun encrypt(secretsJsonString: String): Result {
    val jsonPublicKey = when(val jsonPublicKey = secretsJsonString.extractPublicKey()) {
      is PublicKeyResult.Error -> error("Encryption failed: ${jsonPublicKey.error}")

      is PublicKeyResult.Success -> jsonPublicKey.key
    }

    val encrypter = Encrypter(jsonPublicKey)
    return runCatching {
      Result.Success(secretsJsonString.walkJsonAndTransformStrings(encrypter::encrypt))
    }.getOrElse {
      Result.Error("Encryption failed: ${it.message ?: "no message"}")
    }
  }

  public fun encrypt(ejsonSecretsFile: Path): Result {
    val secretsJsonString = try {
      ejsonSecretsFile.readText()
    }
    catch(t: Throwable) {
      error("Encryption failed: An error occurred while reading ${ejsonSecretsFile.pathString} - ${t.message}")
    }

    return encrypt(secretsJsonString)
  }

  public fun decrypt(secretsJsonString: String, userSuppliedPrivateKey: String? = null): Result {
    val jsonPublicKey = when(val jsonPublicKey = secretsJsonString.extractPublicKey()) {
      is PublicKeyResult.Error -> error("Decryption failed: ${jsonPublicKey.error}")

      is PublicKeyResult.Success -> jsonPublicKey.key
    }

    val jsonPublicKeyEncoded = jsonPublicKey.key.encodeHex()

    val privateKeyString = userSuppliedPrivateKey ?: run {
      val keyDir = keyDirProvider.keyDir ?: error("Decryption failed: couldn't read key file $jsonPublicKeyEncoded")

      try {
        keyDir.resolve(jsonPublicKeyEncoded).readText().trim()
      }
      catch(t: Throwable) {
        error("Decryption failed: couldn't read key file $jsonPublicKeyEncoded (${t.message})")
      }
    }

    val privateKey = privateKeyString.toPrivateKey().getOrThrow()

    val decrypter = Decrypter(privateKey)
    return runCatching {
      Result.Success(secretsJsonString.walkJsonAndTransformStrings(decrypter::decrypt))
    }.getOrElse {
      Result.Error("Decryption failed: ${it.message ?: "no message"}")
    }
  }

  public fun decrypt(ejsonSecretsFile: Path, userSuppliedPrivateKey: String? = null): Result {
    val secretsJsonString = try {
      ejsonSecretsFile.readText()
    }
    catch(t: Throwable) {
      error("Decryption failed: An error occurred while reading ${ejsonSecretsFile.pathString} - ${t.message}")
    }

    return decrypt(secretsJsonString, userSuppliedPrivateKey)
  }

  public sealed class Result {
    public data class Error(val error: String) : Result()

    public data class Success(val json: String) : Result()
  }

  private fun String.extractPublicKey(): PublicKeyResult {
    val publicKey = try {
      extractPublicKeyFromJson()
    }
    catch(t: Throwable) {
      return PublicKeyResult.Error("an error occurred while parsing json - ${t.message}")
    }

    return when(publicKey) {
      null -> PublicKeyResult.Error("public key string not present in EJSON file")
      else -> runCatching {
        PublicKeyResult.Success(publicKey.toPublicKey().getOrThrow())
      }.getOrElse {
        PublicKeyResult.Error(it.message ?: "no message")
      }
    }
  }
}

private sealed class PublicKeyResult {
  data class Error(val error: String) : PublicKeyResult()

  data class Success(val key: PublicKey) : PublicKeyResult()
}
