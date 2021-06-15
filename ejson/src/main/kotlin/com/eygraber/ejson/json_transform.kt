package com.eygraber.ejson

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun JsonObject.walkAndTransformStrings(transform: (String) -> String): JsonObject =
  buildJsonObject {
    forEach { (key, value) ->
      when(value) {
        is JsonPrimitive -> if(
          value.isString &&
          !key.startsWith("_")
        ) {
          put(key, transform(value.content))
        }
        else {
          put(key, value)
        }

        is JsonObject -> put(key, value.walkAndTransformStrings(transform))

        is JsonArray -> put(key, value.walkAndTransformStrings(transform))
      }
    }
  }

internal fun JsonArray.walkAndTransformStrings(transform: (String) -> String): JsonArray =
  buildJsonArray {
    forEach { value ->
      when(value) {
        is JsonPrimitive -> if(
          value.isString
        ) {
          add(transform(value.content))
        }
        else {
          add(value)
        }

        is JsonObject -> add(value.walkAndTransformStrings(transform))

        is JsonArray -> add(value.walkAndTransformStrings(transform))
      }
    }
  }
