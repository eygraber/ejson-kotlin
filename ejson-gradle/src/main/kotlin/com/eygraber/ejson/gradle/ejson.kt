package com.eygraber.ejson.gradle

import com.eygraber.ejson.Ejson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

public inline fun Ejson.decryptSecrets(
  secretsFile: java.nio.file.Path,
  userSuppliedPrivateKey: String? = null,
  transform: (JsonObject) -> String = { it.toString() },
): String = when(
  val result = decrypt(
    ejsonSecretsFile = secretsFile,
    userSuppliedPrivateKey = userSuppliedPrivateKey,
  )
) {
  is Ejson.Result.Success -> transform(
    Json
      .parseToJsonElement(result.json)
      .jsonObject,
  )

  is Ejson.Result.Error -> error(result.error)
}
