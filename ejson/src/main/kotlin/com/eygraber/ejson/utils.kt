package com.eygraber.ejson

import java.math.BigInteger
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.interfaces.XECPrivateKey
import java.security.interfaces.XECPublicKey
import java.util.Locale

internal val boxRegex = Regex("\\AEJ\\[(\\d):([A-Za-z0-9+=/]{44}):([A-Za-z0-9+=/]{32}):(.+)]\\z")

internal fun String.decodeHex() = hexToByteArray()
internal fun ByteArray.encodeHex() = toHexString()

internal fun java.security.PrivateKey.toRawBytes(): ByteArray = (this as XECPrivateKey).scalar.get()

internal fun java.security.PublicKey.toRawBytes(): ByteArray = (this as XECPublicKey).u.toLittleEndian(size = 32)

private fun BigInteger.toLittleEndian(size: Int): ByteArray {
  val bigEndian = toByteArray()
  return ByteArray(size) { i ->
    val index = bigEndian.size - 1 - i
    if(index >= 0) bigEndian[index] else 0
  }
}

// borrowed from https://github.com/NeilMadden/salty-coffee/blob/4b51a3b080df204fd191bdaf7cc2633bb4b8cd43/src/main/java/software/pando/crypto/nacl/Bytes.java#L92
private val PreferredPngs = arrayOf(
  "NativePRNGNonBlocking",
  "NativePRNG",
  "DRBG",
)

internal fun getSecureRandomInstance(): SecureRandom {
  for(alg in PreferredPngs) {
    try {
      return SecureRandom.getInstance(alg)
    }
    catch(e: NoSuchAlgorithmException) {
      // Skip this one
    }
  }

  if(System.getProperty("os.name").lowercase(Locale.US).startsWith("windows")) {
    // On Windows use the SHA1PRNG. While this is a weak algorithm, the default seed source on Windows is
    // native code that calls CryptGenRandom(). By using SecureRandom.generateSeed() we will bypass the
    // weak SHA1PRNG and go straight to this high-quality seed generator.
    try {
      return SecureRandom.getInstance("SHA1PRNG")
    }
    catch(e: NoSuchAlgorithmException) {
      // Skip this one
    }
  }

  error("Unable to find a high-quality SecureRandom source")
}
