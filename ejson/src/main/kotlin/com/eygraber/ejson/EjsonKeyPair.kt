package com.eygraber.ejson

import com.iwebpp.crypto.TweetNaclFast

public class EjsonKeyPair(
  public val secretKey: PrivateKey,
  public val publicKey: PublicKey
) {
  public companion object {
    public fun generate(): EjsonKeyPair =
      TweetNaclFast.Box.keyPair().let { kp ->
        EjsonKeyPair(PrivateKey(kp.secretKey), PublicKey(kp.publicKey))
      }
  }
}

@JvmInline
public value class PrivateKey(public val key: ByteArray) {
  init {
    require(key.size == 32)
  }

  public fun toHexString(): String = key.encodeHex()
}

@JvmInline
public value class PublicKey(public val key: ByteArray) {
  init {
    require(key.size == 32)
  }

  public fun toHexString(): String = key.encodeHex()
}

internal fun String.toPrivateKey(): Result<PrivateKey> = trim().runCatching {
  require(length == 64) {
    "private key has invalid format"
  }

  val bytes = decodeHex()

  require(bytes.size == 32) {
    "private key invalid"
  }

  PrivateKey(bytes)
}

internal fun String.toPublicKey(): Result<PublicKey> = trim().runCatching {
  require(length == 64) {
    "public key has invalid format"
  }

  val bytes = decodeHex()

  require(bytes.size == 32) {
    "public key invalid"
  }

  PublicKey(bytes)
}
