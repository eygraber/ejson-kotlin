package com.eygraber.ejson

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.startsWith
import org.junit.jupiter.api.Test

class EncryptionTests {
  @Test
  fun `encrypting input that just has a public key does not encrypt anything`() = usingFileSystem { fs ->
    fs.withDefaultKeyDir { kp, ejson ->
      val input = """{"_public_key": "${kp.publicKey.toHexString()}"}"""

      assertThat(ejson.assertEncryptSucceededJson(input).string("_public_key")).isEqualTo(kp.publicKey.toHexString())
    }
  }

  @Test
  fun `encrypting input that just has comments does not encrypt anything`() = usingFileSystem { fs ->
    fs.withDefaultKeyDir { kp, ejson ->
      val input = kp.createValidSecretsJson(
        "_comment_one" to "This is a comment",
        "_comment_two" to "This is also a comment"
      )

      assertThat(ejson.assertEncryptSucceeded(input).json).isEqualTo(input)
    }
  }

  @Test
  fun `encrypting input ignores boxed secrets`() = usingFileSystem { fs ->
    fs.withDefaultKeyDir { kp, ejson ->
      assertThat(
        ejson.assertEncryptSucceededJson(
          kp.createValidSecretsJson(BOXED_PAIR)
        ).string(BOXED_SECRET_KEY)
      ).isEqualTo(BOXED_SECRET)
    }
  }

  @Test
  fun `encrypting input ignores boxed secrets and encrypts unboxed secrets`() = usingFileSystem { fs ->
    fs.withDefaultKeyDir { kp, ejson ->
      val encrypted = ejson.assertEncryptSucceededJson(
        kp.createValidSecretsJson(BOXED_PAIR, UNBOXED_PAIR)
      )

      assertThat(encrypted.string(BOXED_SECRET_KEY)).isEqualTo(BOXED_SECRET)
      assertThat(encrypted.string(UNBOXED_SECRET_KEY)).startsWith("EJ[")
    }
  }

  private companion object {
    const val BOXED_SECRET_KEY = "secret"
    @Suppress("MaxLineLength")
    const val BOXED_SECRET = "EJ[1:fjVmA40ot9E0WTIejpeBvMwD3x8oyIo33NOGq0VSslo=:7Acfyhjym4oeZIstXa2EvE1HbSPWP1f9:1SDE4XkfNLRwcZzPLkX9/2YNwJfw3tP1Di1sVDQMYZx9oLm7Ds5v40dJh5Ji]"
    val BOXED_PAIR = BOXED_SECRET_KEY to BOXED_SECRET

    const val UNBOXED_SECRET_KEY = "not secret"
    const val UNBOXED_SECRET = "4, 8, 15, 16, 23, 42"
    val UNBOXED_PAIR = UNBOXED_SECRET_KEY to UNBOXED_SECRET
  }
}
