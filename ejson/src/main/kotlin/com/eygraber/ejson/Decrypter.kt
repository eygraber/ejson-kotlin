package com.eygraber.ejson

import com.iwebpp.crypto.TweetNaclFast
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal class Decrypter internal constructor(
  private val privateKey: PrivateKey,
) {
  @OptIn(ExperimentalEncodingApi::class)
  fun decrypt(message: String): String {
    val error = "invalid message format"

    val groups = requireNotNull(
      boxRegex
        .findAll(message)
        .firstOrNull()
        ?.groupValues,
    ) {
      error
    }

    require(groups.size == 5) {
      error
    }

    val (_, version, publicKey64, nonce64, cipher64) = groups

    requireNotNull(version.toIntOrNull()) {
      error
    }

    val publicKey = PublicKey(Base64.Default.decode(publicKey64))

    val nonce = Base64.Default.decode(nonce64)
    require(nonce.size == 24) {
      "nonce invalid"
    }

    val cipher = Base64.Default.decode(cipher64)

    val cbox = TweetNaclFast.Box(publicKey.key, privateKey.key)
    return cbox.open(cipher, nonce).decodeToString()
  }
}
