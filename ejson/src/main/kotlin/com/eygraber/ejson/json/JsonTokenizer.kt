package com.eygraber.ejson.json

internal class JsonTokenizer(
  private val json: String,
) {
  private var index = 0

  @Suppress("NOTHING_TO_INLINE")
  inline fun nextToken(): JsonToken {
    json.trimStart()
    if(index >= json.length) return JsonToken.EOF()

    return when(val char = json[index]) {
      in whitespaceChars -> JsonToken.Whitespace(char)

      '{' -> JsonToken.BeginObject
      '}' -> JsonToken.EndObject

      '[' -> JsonToken.BeginArray
      ']' -> JsonToken.EndArray

      ',' -> JsonToken.Comma
      ':' -> JsonToken.Colon

      '"' -> parseString()

      in numbers -> {
        val startingIndex = index
        @Suppress("UnconditionalJumpStatementInLoop")
        while(index < json.length && json[index++].isJsonDigit) continue
        JsonToken.Number(json.subSequence(startingIndex..index))
      }

      'n' -> expect(size = 4, expected = "null", JsonToken::Null)

      't' -> expect(size = 4, expected = "true", JsonToken::Boolean)

      'f' -> expect(size = 5, expected = "false", JsonToken::Boolean)

      else -> JsonToken.EOF()
    }.also {
      index++
    }
  }

  private fun expect(size: Int, expected: CharSequence, token: (CharSequence) -> JsonToken) =
    if(index + size - 1 < json.lastIndex) {
      JsonToken.EOF(json.subSequence(index, json.length))
    }
    else {
      val actual = json.subSequence(index, index + size)
      require(actual == expected) {
        "Was expecting $expected; found $actual"
      }

      token(actual)
    }

  private fun parseString(): JsonToken.String {
    val startingIndex = index
    while(index < json.lastIndex) {
      if(json[index++] != '\\' && json[index] == '"') break
    }

    return JsonToken.String(json.substring(startingIndex..index))
  }

  private val Char.isJsonDigit get() = this in numbers || this == '.'

  companion object {
    val whitespaceChars = charArrayOf(' ', '\r', '\n', '\t')
    val numbers = '0'..'9'
  }
}
