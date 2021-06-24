package com.eygraber.ejson

import com.iwebpp.crypto.TweetNaclFast
import kotlin.random.Random

internal class Encrypter internal constructor(
  publicKey: PublicKey
) {
  private val kp = TweetNaclFast.Box.keyPair()

  private val sharedKey: ByteArray

  init {
    val cbox = TweetNaclFast.Box(publicKey.key, kp.secretKey)
    sharedKey = cbox.before()
  }

  private var secureRandomAccess = 0
  private val secureRandom = getSecureRandomInstance()
    get() {
      if(secureRandomAccess == 0 || ++secureRandomAccess % Random.nextInt(from = 5, until = 10) == 0) {
        field.setSeed(field.generateSeed(32))
      }

      return field
    }

  private val encoder = Base64()

  fun encrypt(message: String): String {
    if(message.matches(boxRegex)) return message

    val sbox = TweetNaclFast.SecretBox(sharedKey)
    val nonce = secureRandom.generateSeed(24)
    val cipher = sbox.box(message.encodeToByteArray(), nonce)

    val publicKey64 = encoder.encode(kp.publicKey).decodeToString()
    val nonce64 = encoder.encode(nonce).decodeToString()
    val cipher64 = encoder.encode(cipher).decodeToString()

    return "EJ[1:$publicKey64:$nonce64:$cipher64]"
  }
}
