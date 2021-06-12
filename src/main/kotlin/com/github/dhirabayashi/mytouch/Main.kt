package com.github.dhirabayashi.mytouch

import com.beust.jcommander.JCommander
import com.github.dhirabayashi.mytouch.data.OptionType
import com.github.dhirabayashi.mytouch.data.OptionType.*
import java.lang.NumberFormatException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.system.exitProcess

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
    if(argument.t != null) {
        options[USE_SPECIFIED_TIME] = argument.t
    }
    if(argument.ad != null) {
        options[ADJUST_TIME] = argument.ad
        options[NO_CREATE] = null
    }

    // 終了コード
    var exitCode = 0

    // 実行
    argument.files?.forEach {
        val ret = touch(it, options)
        if(ret != 0) {
            exitCode = ret
        }
    }

    exitProcess(exitCode)
}

fun touch(filename: String, options: Map<OptionType, String?>): Int {
    val file = Path.of(filename)
    if(Files.exists(file)) {

        // 書き換える日付
        val accessTime: FileTime
        val modificationTime: FileTime
        when {
            options.contains(USE_TIMES_FROM_ANOTHER_FILE) -> {
                val refFile = Path.of(options[USE_TIMES_FROM_ANOTHER_FILE])
                if(!Files.exists(refFile)) {
                    System.err.println("mytouch: $refFile: No such file or directory")
                    return 1
                }

                accessTime = Files.getAttribute(refFile, "lastAccessTime") as FileTime
                modificationTime = Files.getLastModifiedTime(refFile)
            }
            options.contains(USE_SPECIFIED_TIME) -> {
                try {
                    val time = parseDate(options[USE_SPECIFIED_TIME]!!)
                    accessTime = time
                    modificationTime = time
                } catch (e: DateTimeParseException) {
                    System.err.println("mytouch: out of range or illegal time specification: [[CC]YY]MMDDhhmm[.SS]")
                    return 1
                }
            }
            options.contains(ADJUST_TIME) -> {
                val num = options[ADJUST_TIME]
                val hh: Long?
                val mm: Long?
                val ss: Long
                try {
                    when {
                        num!!.length == 2 -> {
                            hh = null
                            mm = null
                            ss = num.toLong()
                        }
                        num.length == 4 -> {
                            hh = null
                            mm = num.substring(0, 2).toLong()
                            ss = num.substring(2, 4).toLong()
                        }
                        num.length == 6 -> {
                            hh = num.substring(0, 2).toLong()
                            mm = num.substring(2, 4).toLong()
                            ss = num.substring(4, 6).toLong()
                        }
                        else -> {
                            System.err.println("mytouch: Invalid offset spec, must be [[HH]MM]SS")
                            return 1
                        }
                    }
                } catch (e: NumberFormatException) {
                    System.err.println("mytouch: Invalid offset spec, must be [[HH]MM]SS")
                    return 1
                }

                val currentAccessTime = Files.getAttribute(file, "lastAccessTime") as FileTime
                val currentModTime = Files.getLastModifiedTime(file)

                var accessTimeLdt = LocalDateTime.ofInstant(currentAccessTime.toInstant(), ZoneId.of("Asia/Tokyo"))
                var modTimeLdt = LocalDateTime.ofInstant(currentModTime.toInstant(), ZoneId.of("Asia/Tokyo"))

                if(hh != null) {
                    accessTimeLdt = accessTimeLdt.plusHours(hh)
                    modTimeLdt = modTimeLdt.plusHours(hh)
                }
                if(mm != null) {
                    accessTimeLdt = accessTimeLdt.plusMinutes(mm)
                    modTimeLdt = modTimeLdt.plusMinutes(mm)
                }
                // ss
                accessTimeLdt = accessTimeLdt.plusSeconds(ss)
                modTimeLdt = modTimeLdt.plusSeconds(ss)

                accessTime = FileTime.fromMillis(accessTimeLdt.toEpochMilli())
                modificationTime = FileTime.fromMillis(modTimeLdt.toEpochMilli())
            } else -> {
                val now = LocalDateTime.now(clock).toEpochMilli()
                val time = FileTime.fromMillis(now)
                accessTime = time
                modificationTime = time
            }
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
    return 0
}

fun parseDate(paramDate: String): FileTime {
    val formatter = if(paramDate.contains(".")) {
        DateTimeFormatter.ofPattern("yyyyMMddHHmm.ss")
    } else {
        DateTimeFormatter.ofPattern("yyyyMMddHHmm")
    }

    // 年が2桁指定の場合、69〜99なら1900年代、それ以外なら2000年代となる
    val date = complementCentury(paramDate)

    val epochMilli = LocalDateTime.parse(date, formatter).toEpochMilli()
    return FileTime.fromMillis(epochMilli)
}

fun complementCentury(strDate: String): String {
    // 年が2桁指定の場合、69〜99なら1900年代、それ以外なら2000年代となる
    return if (strDate.length == 10) {
        val yy = strDate.substring(0, 2).toInt()
        if (yy in 69..99) {
            "19$strDate"
        } else {
            "20$strDate"
        }
    } else {
        strDate
    }
}

private fun LocalDateTime.toEpochMilli(): Long {
    return this.toInstant(ZoneOffset.ofHours(9)).toEpochMilli()
}