@file:Suppress("UNCHECKED_CAST")

package com.eygraber.ejson

import java.util.Base64

internal class Base64 {
  private val impl: Any = try {
    Class.forName("java.util.Base64")
    Base64.getEncoder() to Base64.getDecoder()
  }
  catch(_: Throwable) {
    Class.forName("android.util.Base64")
  }

  fun decode(chars: String): ByteArray = try {
    Class.forName("java.util.Base64")
    (impl as Pair<Base64.Encoder, Base64.Decoder>).second.decode(chars)
  }
  catch(_: Throwable) {
    val method = (impl as Class<*>).getMethod("decode", String::class.java, Int::class.java)
    method.invoke(null, chars, 2) as ByteArray
  }

  fun encode(src: ByteArray): ByteArray = try {
    Class.forName("java.util.Base64")
    (impl as Pair<Base64.Encoder, Base64.Decoder>).first.encode(src)
  }
  catch(_: Throwable) {
    val method = (impl as Class<*>).getMethod("encode", ByteArray::class.java, Int::class.java)
    method.invoke(null, src, 2) as ByteArray
  }
}
