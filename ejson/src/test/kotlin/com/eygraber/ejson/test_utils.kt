package com.eygraber.ejson

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows
import java.nio.file.FileSystem
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.writeText

internal inline fun <reified T : Throwable> assertThrowsWithMessage(
  message: String,
  noinline action: () -> Unit
) {
  assertThat(assertThrows<T>(action).message).isEqualTo(message)
}

internal inline fun usingFileSystem(
  fsConfig: Configuration = Configuration.forCurrentPlatform(),
  block: (FileSystem) -> Unit
) {
  Jimfs.newFileSystem(fsConfig).use(block)
}

internal inline fun FileSystem.withDefaultKeyDir(
  block: (EjsonKeyPair, Ejson) -> Unit
) {
  val kp = EjsonKeyPair.generate()

  val keyDir = rootDirectories.first().resolve(getPath("opt", "ejson", "keys")).createDirectories()
  keyDir.resolve(getPath(kp.publicKey.toHexString())).createFile().writeText(
    kp.secretKey.toHexString()
  )

  val ejson = Ejson(filesystem = this)

  block(kp, ejson)
}

internal inline fun FileSystem.withEnvVarKeyDir(
  block: (EjsonKeyPair, Ejson) -> Unit
) {
  val kp = EjsonKeyPair.generate()

  val envvar = System.getenv("EJSON_KEYDIR")

  assertThat(envvar).isNotNull()

  val keyDir = getPath(envvar).createDirectories()
  keyDir.resolve(getPath(kp.publicKey.toHexString())).createFile().writeText(
    kp.secretKey.toHexString()
  )

  val ejson = Ejson(filesystem = this)

  block(kp, ejson)
}

internal inline fun FileSystem.withOverrideKeyDir(
  path: String,
  block: (EjsonKeyPair, Ejson) -> Unit
) {
  val kp = EjsonKeyPair.generate()

  val keyDir = rootDirectories.first().resolve(getPath(path)).createDirectories()
  keyDir.resolve(getPath(kp.publicKey.toHexString())).createFile().writeText(
    kp.secretKey.toHexString()
  )

  val ejson = Ejson(overrideKeyDir = keyDir, filesystem = this)

  block(kp, ejson)
}

internal fun Ejson.assertEncryptSucceeded(input: String): Ejson.Result.Success =
  Assertions.assertDoesNotThrow<Ejson.Result.Success> {
    encrypt(input).assertSucceeded()
  }

internal fun Ejson.assertDecryptSucceeded(input: String): Ejson.Result.Success =
  Assertions.assertDoesNotThrow<Ejson.Result.Success> {
    decrypt(input).assertSucceeded()
  }

internal fun Ejson.assertEncryptSucceededJson(input: String): JsonObject =
  assertEncryptSucceeded(input).toJson()

internal fun Ejson.assertDecryptSucceededJson(input: String): JsonObject =
  Assertions.assertDoesNotThrow<JsonObject> {
    assertDecryptSucceeded(input).toJson()
  }

internal fun Ejson.Result.assertSucceeded(): Ejson.Result.Success {
  assertThat(this).isInstanceOf(Ejson.Result.Success::class)
  return this as Ejson.Result.Success
}

internal fun Ejson.Result.Success.toJson() = Json.Default.parseToJsonElement(json).jsonObject

internal fun Ejson.assertDecryptFailed(input: String): Ejson.Result.Error =
  Assertions.assertDoesNotThrow<Ejson.Result.Error> {
    decrypt(input).assertFailed()
  }

internal fun Ejson.Result.assertFailed(): Ejson.Result.Error {
  assertThat(this).isInstanceOf(Ejson.Result.Error::class)
  return this as Ejson.Result.Error
}

internal fun EjsonKeyPair.createValidSecretsJson(
  keyValue: Pair<String, String>,
  vararg keyValues: Pair<String, String>
) =
  """
  |{
  |"_public_key": "${publicKey.toHexString()}",
  |"${keyValue.first}": "${keyValue.second}"
  |${if(keyValues.isEmpty()) "" else keyValues.joinToString(prefix = ",") { """"${it.first}": "${it.second}"""" }}
  |}
  """.trimMargin()
