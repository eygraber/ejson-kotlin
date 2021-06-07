package com.eygraber.ejson.gradle

open class EjsonExtension {
  var onSecretsDecrypted: () -> Unit = {}
  var onEjsonFailure: ((variant: String) -> Boolean) = { true }
}
