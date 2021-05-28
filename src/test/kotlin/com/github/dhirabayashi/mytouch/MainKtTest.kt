package com.github.dhirabayashi.mytouch

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

internal class MainKtTest {

    @Test
    fun test_touch_newFile(@TempDir tempDir: Path) {
        // setup
        val file = tempDir.resolve("test.txt")

        // run
        touch(file.toAbsolutePath().toString())

        // verify
        assertTrue(Files.exists(file))
    }

    @Test
    fun test_touch_existingFile(@TempDir tempDir: Path) {
        // setup
        val file = tempDir.resolve("test.txt")
        Files.createFile(file)

        val ldt = LocalDateTime.of(2021, 5, 28, 21, 53)
        val instant = ldt.toInstant(ZoneOffset.ofHours(9))
        clock = Clock.fixed(instant, ZoneId.of("Asia/Tokyo"))

        // run
        touch(file.toAbsolutePath().toString())

        // verify
        assertTrue(Files.exists(file))
        assertEquals(FileTime.fromMillis(instant.toEpochMilli()), Files.getLastModifiedTime(file))
    }
}