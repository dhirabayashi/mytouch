package com.github.dhirabayashi.mytouch

import com.github.dhirabayashi.mytouch.data.OptionType.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.*

internal class MainKtTest {

    @Test
    fun test_touch_newFile(@TempDir tempDir: Path) {
        // setup
        val file = tempDir.resolve("test.txt")

        // run
        touch(file.toAbsolutePath().toString(), mapOf())

        // verify
        assertTrue(Files.exists(file))
    }

    @Test
    fun test_touch_existingFile(@TempDir tempDir: Path) {
        // setup
        val file = tempDir.resolve("test.txt")
        Files.createFile(file)

        val instant = instantOf(2021, 5, 28, 21, 53)
        clock = fixedClock(instant)

        // run
        val options = mapOf(CHANGE_ACCESS_TIME to null, CHANGE_MODIFICATION_TIME to null)
        touch(file.toAbsolutePath().toString(), options)
        // verify
        assertTrue(Files.exists(file))
        val expected = FileTime.fromMillis(instant.toEpochMilli())
        assertEquals(expected, Files.getLastModifiedTime(file))
        assertEquals(expected, Files.getAttribute(file, "lastAccessTime"))
    }

    @Test
    fun test_touch_newFile_noCreate(@TempDir tempDir: Path) {
        // setup
        val file = tempDir.resolve("test.txt")

        // run
        touch(file.toAbsolutePath().toString(), mapOf(NO_CREATE to null))

        // verify
        assertFalse(Files.exists(file))
    }

    @Test
    fun test_touch_onlyAccessTime(@TempDir tempDir: Path) {
        // setup
        val file = tempDir.resolve("test.txt")
        Files.createFile(file)

        val instant = instantOf(2021, 5, 28, 21, 53)
        clock = fixedClock(instant)

        // run
        touch(file.toAbsolutePath().toString(), mapOf(CHANGE_ACCESS_TIME to null))

        // verify
        assertTrue(Files.exists(file))
        val expected = FileTime.fromMillis(instant.toEpochMilli())
        assertNotEquals(expected, Files.getLastModifiedTime(file))
        assertEquals(expected, Files.getAttribute(file, "lastAccessTime"))
    }

    @Test
    fun test_touch_onlyModificationTime(@TempDir tempDir: Path) {
        // setup
        val file = tempDir.resolve("test.txt")
        Files.createFile(file)

        val instant = instantOf(2021, 5, 28, 21, 53)
        clock = fixedClock(instant)

        // run
        touch(file.toAbsolutePath().toString(), mapOf(CHANGE_MODIFICATION_TIME to null))

        // verify
        assertTrue(Files.exists(file))
        val expected = FileTime.fromMillis(instant.toEpochMilli())
        assertEquals(expected, Files.getLastModifiedTime(file))
        assertNotEquals(expected, Files.getAttribute(file, "lastAccessTime"))
    }

    @Test
    fun test_touch_useTimeFromAnotherFile(@TempDir tempDir: Path) {
        // setup
        val targetFile = tempDir.resolve("target.txt")
        Files.createFile(targetFile)
        val anotherFile = tempDir.resolve("another.txt")
        Files.createFile(anotherFile)

        val accessTime = FileTime.fromMillis(instantOf(2019, 1, 2, 3, 4).toEpochMilli())
        val modificationTime = FileTime.fromMillis(instantOf(2020, 2, 3, 4 ,5).toEpochMilli())
        Files.setAttribute(anotherFile, "lastAccessTime", accessTime)
        Files.setLastModifiedTime(anotherFile, modificationTime)

        // run
        val options = mapOf(USE_TIMES_FROM_ANOTHER_FILE to anotherFile.toAbsolutePath().toString(),
            CHANGE_MODIFICATION_TIME to null, CHANGE_ACCESS_TIME to null)
        touch(targetFile.toAbsolutePath().toString(), options)

        // verify
        assertEquals(accessTime, Files.getAttribute(targetFile, "lastAccessTime"))
        assertEquals(modificationTime, Files.getLastModifiedTime(targetFile))
    }

    @Test
    fun test_touch_useTimeNonexistentFile(@TempDir tempDir: Path) {
        // setup
        val targetFile = tempDir.resolve("target.txt")
        Files.createFile(targetFile)

        // run
        val options = mapOf(USE_TIMES_FROM_ANOTHER_FILE to "nonexistent.txt",
            CHANGE_MODIFICATION_TIME to null, CHANGE_ACCESS_TIME to null)
        // 例外が投げられない
        touch(targetFile.toAbsolutePath().toString(), options)
    }

    private fun instantOf(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int): Instant {
        val ldt = LocalDateTime.of(year, month, dayOfMonth, hour, minute)
        return ldt.toInstant(ZoneOffset.ofHours(9))
    }

    private fun fixedClock(instant: Instant): Clock {
        return Clock.fixed(instant, ZoneId.of("Asia/Tokyo"))
    }
}