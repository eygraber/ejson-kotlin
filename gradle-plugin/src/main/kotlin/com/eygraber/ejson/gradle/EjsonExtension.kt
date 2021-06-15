package com.eygraber.ejson.gradle

public open class EjsonExtension {
  public var onSecretsDecrypted: () -> Unit = {}
  public var onEjsonFailure: ((variant: String) -> Boolean) = { true }
}
