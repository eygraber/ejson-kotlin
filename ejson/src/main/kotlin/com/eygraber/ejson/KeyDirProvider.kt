package com.eygraber.ejson

import java.nio.file.FileSystem
import java.nio.file.Path
import kotlin.io.path.exists

internal class KeyDirProvider(
  fs: FileSystem,
  overrideKeyDir: Path?,
) {
  val keyDir by lazy {
    overrideKeyDir
      ?: System.getenv("EJSON_KEYDIR")?.let { fs.getPath(it) }?.takeIf(Path::exists)
      ?: fs.rootDirectories.first().resolve(fs.getPath("opt", "ejson", "keys")).takeIf(Path::exists)
  }
}
