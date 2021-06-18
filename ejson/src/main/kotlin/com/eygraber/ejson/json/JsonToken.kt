package com.eygraber.ejson.json

internal sealed class JsonToken {
  object BeginObject : JsonToken()
  object EndObject : JsonToken()

  object BeginArray : JsonToken()
  object EndArray : JsonToken()

  object Comma : JsonToken()
  object Colon : JsonToken()

  class String(val content: CharSequence) : JsonToken()

  class Boolean(val content: CharSequence) : JsonToken()

  class Number(val content: CharSequence) : JsonToken()

  class Null(val content: CharSequence) : JsonToken()

  class Whitespace(val content: Char) : JsonToken()

  class EOF(val content: CharSequence = "") : JsonToken()

  override fun toString(): kotlin.String = javaClass.simpleName
}
