package com.p3achb0t.client.util

import com.p3achb0t.client.configs.Constants
import java.io.File
import java.io.InputStream
import java.net.Socket
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


class Util {

    companion object {
        fun checkClientRevision(revision: Int, timeout: Int): Boolean {

            val socket = Socket(Constants.GAME_WORLD_BASE, 43594)
            socket.getOutputStream().write(byteArrayOf(15, (revision shr 24 and 0xFF).toByte(), (revision shr 16 and 0xFF).toByte(), (revision shr 8 and 0xFF).toByte(), (revision and 0xFF).toByte()))
            socket.soTimeout = timeout
            val response = socket.getInputStream().read()
            socket.close()
            if (response != 0) {
                return false
            }
            return true
        }

        fun createDirIfNotExist(path: String) {
            if (!Files.exists(Paths.get(path ))) {
                File(path).mkdirs()
            }
        }

        fun createAllDirs() {
            createDirIfNotExist(Path.of(Constants.USER_DIR, Constants.APPLICATION_CACHE_DIR, Constants.JARS_DIR).toString())
            createDirIfNotExist("cache")
        }

        fun readConfig(path: String) : String {
            val file = File(path)
            val ins: InputStream = file.inputStream()
            val content = ins.readBytes().toString(Charset.defaultCharset())
            return content

        }
    }


}