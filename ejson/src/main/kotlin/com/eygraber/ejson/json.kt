package com.eygraber.ejson

import com.eygraber.ejson.json.JsonToken
import com.eygraber.ejson.json.JsonTokenizer

internal fun String.walkJsonAndTransformStrings(transform: (String) -> String): String {
  val tokenizer = JsonTokenizer(this)
  val builder = StringBuilder(length)

  var isExpectingValue = false
  var key: CharSequence? = null

  while(true) {
    when(val token = tokenizer.nextToken()) {
      JsonToken.BeginObject -> {
        builder.append("{")
        key = null
        isExpectingValue = false
      }

      JsonToken.EndObject -> builder.append("}")

      JsonToken.BeginArray -> {
        builder.append("[")
        key = null
        isExpectingValue = false
      }

      JsonToken.EndArray -> builder.append("]")

      JsonToken.Colon -> {
        builder.append(":")
        isExpectingValue = true
      }

      JsonToken.Comma -> builder.append(",")

      is JsonToken.Boolean -> {
        builder.append(token.content)
        key = null
        isExpectingValue = false
      }

      is JsonToken.Null -> {
        builder.append(token.content)
        key = null
        isExpectingValue = false
      }

      is JsonToken.Number -> {
        builder.append(token.content)
        key = null
        isExpectingValue = false
      }

      is JsonToken.String -> when {
        isExpectingValue -> {
          requireNotNull(key)
          if(key.length >= 2 && key[1] == '_') {
            builder.append(token.content)
          }
          else {
            val unquoted = token.content.substring(1 until token.content.lastIndex)
            builder.append("\"${transform(unquoted)}\"")
          }

          key = null
          isExpectingValue = false
        }

        else -> {
          key = token.content
          builder.append(token.content)
        }
      }

      is JsonToken.Whitespace -> builder.append(token.content)

      is JsonToken.EOF -> {
        builder.append(token.content)
        break
      }
    }
  }

  return builder.toString()
}

internal fun String.extractPublicKeyFromJson(): String? {
  val tokenizer = JsonTokenizer(this)

  var firstToken = tokenizer.nextToken()
  while(firstToken is JsonToken.Whitespace) {
    firstToken = tokenizer.nextToken()
  }

  require(firstToken is JsonToken.BeginObject) {
    error("expected a JsonObject but received $firstToken")
  }

  var isExpectingValue = false
  var key: CharSequence? = null

  while(true) {
    when(val token = tokenizer.nextToken()) {
      JsonToken.Colon -> isExpectingValue = true

      is JsonToken.String -> when {
        isExpectingValue -> {
          requireNotNull(key)
          if(key == "\"_public_key\"") return token.content.substring(1 until token.content.lastIndex)

          key = null
          isExpectingValue = false
        }

        else -> key = token.content
      }

      is JsonToken.Boolean,
      is JsonToken.Null,
      is JsonToken.Number,
      JsonToken.BeginObject,
      JsonToken.BeginArray,
      JsonToken.Comma,
      JsonToken.EndArray,
      JsonToken.EndObject,
      -> {
        key = null
        isExpectingValue = false
      }

      is JsonToken.Whitespace -> {}

      is JsonToken.EOF -> return null
    }
  }
}
