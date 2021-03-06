package com.eygraber.ejson

import com.iwebpp.crypto.TweetNaclFast

internal class Decrypter internal constructor(
  private val privateKey: PrivateKey
) {
  private val decoder = Base64()

  fun decrypt(message: String): String {
    val error = "invalid message format"

    val groups = requireNotNull(
      boxRegex
        .findAll(message)
        .firstOrNull()
        ?.groupValues
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

    val publicKey = PublicKey(decoder.decode(publicKey64))

    val nonce = decoder.decode(nonce64)
    require(nonce.size == 24) {
      "nonce invalid"
    }

    val cipher = decoder.decode(cipher64)

    val cbox = TweetNaclFast.Box(publicKey.key, privateKey.key)
    return cbox.open(cipher, nonce).decodeToString()
  }
}
