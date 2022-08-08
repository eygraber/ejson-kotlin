package com.eygraber.ejson

import org.junit.jupiter.api.Test
import kotlin.io.path.name
import kotlin.io.path.pathString

class InputTests {
  @Test
  fun `when input is empty an error is thrown`() {
    val ejson = Ejson()

    assertThrowsWithMessage<IllegalStateException>(
      "Encryption failed: an error occurred while parsing json - expected a JsonObject but received EOF"
    ) {
      ejson.encrypt("")
    }

    assertThrowsWithMessage<IllegalStateException>(
      "Decryption failed: an error occurred while parsing json - expected a JsonObject but received EOF"
    ) {
      ejson.decrypt("")
    }
  }

  @Test
  fun `when input doesn't contain a json element an error is thrown`() {
    val ejson = Ejson()

    assertThrowsWithMessage<IllegalStateException>(
      "Encryption failed: an error occurred while parsing json - expected a JsonObject but received String"
    ) {
      ejson.encrypt("\"test\": \"true\"")
    }

    assertThrowsWithMessage<IllegalStateException>(
      "Decryption failed: an error occurred while parsing json - expected a JsonObject but received String"
    ) {
      ejson.decrypt("\"test\": \"true\"")
    }
  }

  @Test
  fun `when input has a top level array an error is thrown`() {
    val ejson = Ejson()

    assertThrowsWithMessage<IllegalStateException>(
      "Encryption failed: an error occurred while parsing json - expected a JsonObject but received BeginArray"
    ) {
      ejson.encrypt("[]")
    }

    assertThrowsWithMessage<IllegalStateException>(
      "Decryption failed: an error occurred while parsing json - expected a JsonObject but received BeginArray"
    ) {
      ejson.decrypt("[]")
    }
  }

  @Test
  fun `when input has a top level object an error is thrown`() {
    val ejson = Ejson()

    assertThrowsWithMessage<IllegalStateException>(
      "Encryption failed: public key string not present in EJSON file"
    ) {
      ejson.encrypt("{}")
    }

    assertThrowsWithMessage<IllegalStateException>(
      "Decryption failed: public key string not present in EJSON file"
    ) {
      ejson.decrypt("{}")
    }
  }

  @Test
  fun `when input doesn't have a public key field an error is thrown`() {
    val ejson = Ejson()

    val input = """
                |{
                |  "public_key": "test"
                |}
                """.trimMargin()

    assertThrowsWithMessage<IllegalStateException>(
      "Encryption failed: public key string not present in EJSON file"
    ) {
      ejson.encrypt(input)
    }

    assertThrowsWithMessage<IllegalStateException>(
      "Decryption failed: public key string not present in EJSON file"
    ) {
      ejson.decrypt(input)
    }
  }

  @Test
  fun `when input has an invalid public key field an error is thrown`() {
    val ejson = Ejson()

    val input = """
                |{
                |  "_public_key": "invalid"
                |}
                """.trimMargin()

    assertThrowsWithMessage<IllegalStateException>(
      "Encryption failed: public key has invalid format (length=7)"
    ) {
      ejson.encrypt(input)
    }

    assertThrowsWithMessage<IllegalStateException>(
      "Decryption failed: public key has invalid format (length=7)"
    ) {
      ejson.decrypt(input)
    }
  }

  @Test
  fun `when the input file doesn't exist an error is thrown`() = usingFileSystem { fs ->
    val input = fs.getPath("test")
    val inputPath = input.pathString

    val ejson = Ejson()

    assertThrowsWithMessage<IllegalStateException>(
      "Encryption failed: An error occurred while reading $inputPath - ${input.name}"
    ) {
      ejson.encrypt(input)
    }

    assertThrowsWithMessage<IllegalStateException>(
      "Decryption failed: An error occurred while reading $inputPath - ${input.name}"
    ) {
      ejson.decrypt(input)
    }
  }
}
