package com.github.dhirabayashi.mytouch

import com.beust.jcommander.JCommander
import java.nio.file.Files
import java.nio.file.Path

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

fun touch(file: String) {
    Files.createFile(Path.of(file))
}