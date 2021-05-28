package com.github.dhirabayashi.mytouch

import com.beust.jcommander.JCommander
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset

var clock: Clock = Clock.systemDefaultZone()

fun main(args: Array<String>) {
    val argument = Argument()
    JCommander.newBuilder()
        .addObject(argument)
        .build()
        .parse(*args)

    argument.files?.forEach {
        touch(it)
    }
}

fun touch(filename: String) {
    val file = Path.of(filename)
    if(Files.exists(file)) {
        val now = LocalDateTime.now(clock).toInstant(ZoneOffset.ofHours(9)).toEpochMilli()
        Files.setLastModifiedTime(file, FileTime.fromMillis(now))
    } else {
        Files.createFile(file)
    }
}