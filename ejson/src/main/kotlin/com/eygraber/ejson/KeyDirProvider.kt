package com.eygraber.ejson

import java.nio.file.FileSystem
import java.nio.file.Path
import kotlin.io.path.exists

internal class KeyDirProvider(
  fs: Any,
  overrideKeyDir: Path?
) {
  val keyDir by lazy {
    if(isFileSystemSupported()) {
      fs as FileSystem
      overrideKeyDir
        ?: System.getenv("EJSON_KEYDIR")?.let { fs.getPath(it) }?.takeIf(Path::exists)
        ?: fs.rootDirectories.first().resolve(fs.getPath("opt", "ejson", "keys")).takeIf(Path::exists)
    }
    else {
      overrideKeyDir
        ?: System.getenv("EJSON_KEYDIR")?.let { Path.of(it) }?.takeIf(Path::exists)
        ?: Path.of("/", "opt", "ejson", "keys").takeIf(Path::exists)
    }
  }
}

private fun isFileSystemSupported() =
  try {
    Class.forName("java.nio.file.FileSystems")
    true
  }
  catch(_: Throwable) {
    false
  }
