package com.eygraber.ejson

import software.pando.crypto.nacl.CryptoBox
import software.pando.crypto.nacl.SecretBox
import java.security.Key
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random

internal class Encrypter internal constructor(
  publicKey: PublicKey,
) {
  private val kp = CryptoBox.keyPair()

  private val sharedKey: Key = CryptoBox.agree(kp.private, CryptoBox.publicKey(publicKey.key))

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

    val nonce = secureRandom.generateSeed(24)
    val cipher = SecretBox.encrypt(sharedKey, nonce, message.encodeToByteArray()).ciphertextWithTag

    val publicKey64 = Base64.Default.encode(kp.public.toRawBytes())
    val nonce64 = Base64.Default.encode(nonce)
    val cipher64 = Base64.Default.encode(cipher)

    return "EJ[1:$publicKey64:$nonce64:$cipher64]"
  }
}
