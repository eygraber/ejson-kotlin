#!/usr/bin/env kscript

@file:DependsOn("com.eygraber:ejson-kotlin:1.0.0")

import com.eygraber.ejson.Ejson
import com.eygraber.ejson.EjsonKeyPair
import java.nio.file.Path
import kotlin.io.path.writeText

require(args.isNotEmpty())

when(args[0]) {
  "keygen" -> keygen()
  "encrypt" -> {
    require(args.size == 2)
    encrypt(args[1])
  }
  "decrypt" -> {
    require(args.size == 2)
    decrypt(args[1])
  }
}

fun keygen() {
  val kp = EjsonKeyPair.generate()

  println("Public Key:")
  println(kp.publicKey.toHexString())
  println("Private Key:")
  println(kp.secretKey.toHexString())
}

fun encrypt(path: String) {
  val filePath = Path.of(path)
  when(val result = Ejson().encrypt(filePath)) {
    is Ejson.Result.Success -> {
      filePath.writeText(result.json)
      println(result.json)
    }
    is Ejson.Result.Error -> println(result.error)
  }
}

fun decrypt(path: String) {
  when(val result = Ejson().decrypt(Path.of(path))) {
    is Ejson.Result.Success -> println(result.json)
    is Ejson.Result.Error -> println(result.error)
  }
}
