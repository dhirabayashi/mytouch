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
    // コマンドライン引数の解析
    val argument = Argument()
    JCommander.newBuilder()
        .addObject(argument)
        .build()
        .parse(*args)

    val options = mutableListOf<Option>()
    if(argument.c) {
        options.add(Option.NO_CREATE)
    }

    // 実行
    argument.files?.forEach {
        touch(it, *options.toTypedArray())
    }
}

fun touch(filename: String, vararg options: Option) {
    val optionsSet = options.toHashSet()
    val file = Path.of(filename)
    if(Files.exists(file)) {
        val now = LocalDateTime.now(clock).toInstant(ZoneOffset.ofHours(9)).toEpochMilli()
        val time = FileTime.fromMillis(now)
        Files.setLastModifiedTime(file, time)
        Files.setAttribute(file, "lastAccessTime", time)
    } else if(!optionsSet.contains(Option.NO_CREATE)) {
        Files.createFile(file)
    }
}

enum class Option {
    /**
     * ファイルを作成しない
     */
    NO_CREATE
}