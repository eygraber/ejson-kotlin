package com.eygraber.ejson

import com.google.common.jimfs.Configuration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import uk.org.webcompere.systemstubs.jupiter.SystemStub
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension

@ExtendWith(SystemStubsExtension::class)
class KeyDirTests {
  @Test
  fun `when there is no keydir an error is thrown`() = usingFileSystem { fs ->
    val ejson = Ejson(filesystem = fs)

    assertThrowsWithMessage<IllegalStateException>(
      "Decryption failed: couldn't read key file $PUBLIC_KEY",
    ) {
      ejson.decrypt(
        """
      |{"_public_key": "$PUBLIC_KEY"}
        """.trimMargin(),
      )
    }
  }

  @Test
  fun `when the keydir doesn't exist an error is thrown`() = usingFileSystem { fs ->
    val ejson = Ejson(
      overrideKeyDir = fs.getPath("fake"),
      filesystem = fs,
    )

    assertThrowsWithMessage<IllegalStateException>(
      "Decryption failed: couldn't read key file $PUBLIC_KEY (fake/$PUBLIC_KEY)",
    ) {
      ejson.decrypt(
        """
      |{"_public_key": "$PUBLIC_KEY"}
        """.trimMargin(),
      )
    }
  }

  @Test
  fun `when the default keydir is used no error is thrown`() = usingFileSystem { fs ->
    fs.withDefaultKeyDir { kp, ejson ->
      val encrypted = ejson.assertEncryptSucceededJson(
        kp.createValidSecretsJson("secret" to "Keep it secret, keep it safe!"),
      )

      ejson.assertDecryptSucceededJson(encrypted.toString())
    }
  }

  @SystemStub
  @Suppress("unused")
  private val envVars = EnvironmentVariables("EJSON_KEYDIR", "/tmp/ejsonKeyDir")

  @Test
  fun `when the envvar keydir is used no error is thrown`() = usingFileSystem(Configuration.unix()) { fs ->
    fs.withEnvVarKeyDir { kp, ejson ->
      val encrypted = ejson.assertEncryptSucceededJson(
        kp.createValidSecretsJson("secret" to "Keep it secret, keep it safe!"),
      )

      ejson.assertDecryptSucceededJson(encrypted.toString())
    }
  }

  @Test
  fun `when the override keydir is used no error is thrown`() = usingFileSystem { fs ->
    fs.withOverrideKeyDir("some/path") { kp, ejson ->
      val encrypted = ejson.assertEncryptSucceededJson(
        kp.createValidSecretsJson("secret" to "Keep it secret, keep it safe!"),
      )

      ejson.assertDecryptSucceededJson(encrypted.toString())
    }
  }

  companion object {
    const val PUBLIC_KEY = "1234567890123456789012345678901234567890123456789012345678901234"
  }
}
