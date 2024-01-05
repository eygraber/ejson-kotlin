package com.eygraber.ejson

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class DecryptionTests {
  @Test
  fun `decrypting input that just has a public key does not decrypt anything`() = usingFileSystem { fs ->
    fs.withDefaultKeyDir { kp, ejson ->
      val input = """{"_public_key": "${kp.publicKey.toHexString()}"}"""

      assertThat(ejson.assertDecryptSucceededJson(input).string("_public_key")).isEqualTo(kp.publicKey.toHexString())
    }
  }

  @Test
  fun `decrypting input that just has comments does not decrypt anything`() = usingFileSystem { fs ->
    fs.withDefaultKeyDir { kp, ejson ->
      val input = kp.createValidSecretsJson(
        "_comment_one" to "This is a comment",
        "_comment_two" to "This is also a comment",
      )

      assertThat(ejson.assertDecryptSucceeded(input).json).isEqualTo(input)
    }
  }

  @Test
  fun `decrypting input that is not boxed throws an error`() = usingFileSystem { fs ->
    fs.withDefaultKeyDir { kp, ejson ->
      assertThat(
        ejson.assertDecryptFailed(
          kp.createValidSecretsJson(UNBOXED_PAIR),
        ).error,
      ).isEqualTo("Decryption failed: invalid message format")
    }
  }

  @Test
  fun `decrypting actually works`() = usingFileSystem { fs ->
    fs.withDefaultKeyDir { kp, ejson ->
      val input = kp.createValidSecretsJson(UNBOXED_PAIR)
      val encrypted = ejson.assertEncryptSucceeded(
        input,
      ).json

      assertThat(
        ejson.assertDecryptSucceeded(encrypted).json,
      ).isEqualTo(input)
    }
  }

  @Test
  fun `decrypting with an user supplied private key actually works`() = usingFileSystem { fs ->
    val kp = EjsonKeyPair.generate()
    val ejson = Ejson(filesystem = fs)

    val input = kp.createValidSecretsJson(UNBOXED_PAIR)
    val encrypted = ejson.assertEncryptSucceeded(
      input,
    ).json

    val privateKey = kp.secretKey.toHexString()

    assertThat(
      ejson.assertDecryptSucceeded(encrypted, userSuppliedPrivateKey = privateKey).json,
    ).isEqualTo(input)
  }

  private companion object {
    const val UNBOXED_SECRET_KEY = "not secret"
    const val UNBOXED_SECRET = "4, 8, 15, 16, 23, 42"
    val UNBOXED_PAIR = UNBOXED_SECRET_KEY to UNBOXED_SECRET
  }
}
