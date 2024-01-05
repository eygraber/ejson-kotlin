package com.eygraber.ejson

import com.iwebpp.crypto.TweetNaclFast
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random

internal class Encrypter internal constructor(
  publicKey: PublicKey,
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

  @OptIn(ExperimentalEncodingApi::class)
  fun encrypt(message: String): String {
    if(message.matches(boxRegex)) return message

    val sbox = TweetNaclFast.SecretBox(sharedKey)
    val nonce = secureRandom.generateSeed(24)
    val cipher = sbox.box(message.encodeToByteArray(), nonce)

    val publicKey64 = Base64.Default.encode(kp.publicKey)
    val nonce64 = Base64.Default.encode(nonce)
    val cipher64 = Base64.Default.encode(cipher)

    return "EJ[1:$publicKey64:$nonce64:$cipher64]"
  }
}
