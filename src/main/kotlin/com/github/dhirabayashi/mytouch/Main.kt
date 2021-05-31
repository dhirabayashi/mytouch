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
    if(argument.a) {
        options.add(Option.CHANGE_ACCESS_TIME)
    }
    if(argument.m) {
        options.add(Option.CHANGE_MODIFICATION_TIME)
    }
    // aオプションとmオプションのいずれも指定されない場合は、両方指定したのと同じ動作にする
    if(!argument.a && !argument.m) {
        options.add(Option.CHANGE_ACCESS_TIME)
        options.add(Option.CHANGE_MODIFICATION_TIME)
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

        if(optionsSet.contains(Option.CHANGE_MODIFICATION_TIME)) {
            Files.setLastModifiedTime(file, time)
        }

        if(optionsSet.contains(Option.CHANGE_ACCESS_TIME)) {
            Files.setAttribute(file, "lastAccessTime", time)
        }
    } else if(!optionsSet.contains(Option.NO_CREATE)) {
        Files.createFile(file)
    }
}

enum class Option {
    /**
     * ファイルを作成しない
     */
    NO_CREATE,

    /**
     * 最終アクセス日時を更新する
     */
    CHANGE_ACCESS_TIME,

    /**
     * 最終更新日時を更新する
      */
    CHANGE_MODIFICATION_TIME,
}