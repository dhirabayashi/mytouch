package com.github.dhirabayashi.mytouch

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

internal class MainKtTest {

    @Test
    fun test_touch(@TempDir tempDir: Path) {
        // setup
        val file = tempDir.resolve("test.txt")

        // run
        touch(file.toAbsolutePath().toString())

        // verify
        assertTrue(Files.exists(file))
    }
}