package com.eygraber.ejson

import software.pando.crypto.nacl.SecretBox
import kotlin.random.Random

class Ejson {
  fun encrypt(key: ByteArray, message: String) {
    SecretBox.encrypt(
      SecretBox.key(key),
      generateNonce(),
      message.encodeToByteArray()
    )
  }

  fun decrypt(key: ByteArray, nonce: ByteArray, message: String) =
    SecretBox.fromDetached(
      nonce,
      message.encodeToByteArray(),
      byteArrayOf()
    ).decryptToString(SecretBox.key(key))

  private fun generateNonce() = Random.nextBytes(24)
}
