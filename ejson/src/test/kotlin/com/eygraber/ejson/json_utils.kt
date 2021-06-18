@file:Suppress("NOTHING_TO_INLINE")

package com.eygraber.ejson

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

inline val JsonElement?.asJsonObjectOrNull
  get() = runCatching {
    this?.jsonObject
  }.getOrNull()

inline val JsonElement?.asJsonArrayOrNull
  get() = runCatching {
    this?.jsonArray
  }.getOrNull()

inline val JsonElement?.asJsonPrimitiveOrNull
  get() = runCatching {
    this?.jsonPrimitive
  }.getOrNull()

inline val JsonElement.asString
  get() = jsonPrimitive.content

inline val JsonElement?.asStringOrNull
  get() = asJsonPrimitiveOrNull?.contentOrNull

inline fun JsonObject.string(key: String) = requireNotNull(key).asString

inline fun JsonObject.stringOr(key: String, default: String) = stringOrNull(key) ?: default

inline fun JsonObject.stringOr(key: String, default: () -> String) = stringOrNull(key) ?: default()

inline fun JsonObject.stringOrEmpty(key: String) = stringOr(key, "")

inline fun JsonObject.stringOrNull(key: String) = get(key)?.asStringOrNull

@PublishedApi internal inline fun JsonObject.requireNotNull(key: String) =
  requireNotNull(get(key)) {
    """The key "$key" does not exist in $keys"""
  }
