package com.github.dhirabayashi.mytouch

import com.beust.jcommander.JCommander
import com.github.dhirabayashi.mytouch.data.OptionType
import com.github.dhirabayashi.mytouch.data.OptionType.*
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

    val options = mutableMapOf<OptionType, String?>()
    if(argument.c) {
        options[NO_CREATE] = null
    }
    if(argument.a) {
        options[CHANGE_ACCESS_TIME] = null
    }
    if(argument.m) {
        options[CHANGE_MODIFICATION_TIME] = null
    }
    // aオプションとmオプションのいずれも指定されない場合は、両方指定したのと同じ動作にする
    if(!argument.a && !argument.m) {
        options[CHANGE_ACCESS_TIME] = null
        options[CHANGE_MODIFICATION_TIME] = null
    }
    if(argument.r != null) {
        options[USE_TIMES_FROM_ANOTHER_FILE] = argument.r
    }

    // 実行
    argument.files?.forEach {
        touch(it, options)
    }
}

fun touch(filename: String, options: Map<OptionType, String?>) {
    val file = Path.of(filename)
    if(Files.exists(file)) {

        // 書き換える日付
        val accessTime: FileTime
        val modificationTime: FileTime
        if(options.contains(USE_TIMES_FROM_ANOTHER_FILE)) {
            val refFile = Path.of(options[USE_TIMES_FROM_ANOTHER_FILE])
            accessTime = Files.getAttribute(refFile, "lastAccessTime") as FileTime
            modificationTime = Files.getLastModifiedTime(refFile)
        } else {
            val now = LocalDateTime.now(clock).toInstant(ZoneOffset.ofHours(9)).toEpochMilli()
            val time = FileTime.fromMillis(now)
            accessTime = time
            modificationTime = time
        }

        if(options.contains(CHANGE_MODIFICATION_TIME)) {
            Files.setLastModifiedTime(file, modificationTime)
        }

        if(options.contains(CHANGE_ACCESS_TIME)) {
            Files.setAttribute(file, "lastAccessTime", accessTime)
        }
    } else if(!options.contains(NO_CREATE)) {
        Files.createFile(file)
    }
}