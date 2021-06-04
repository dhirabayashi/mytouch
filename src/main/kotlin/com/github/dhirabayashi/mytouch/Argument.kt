package com.github.dhirabayashi.mytouch

import com.beust.jcommander.Parameter

class Argument {
    @Parameter(description = "files")
    internal var files: List<String>? = null

    @Parameter(names = ["-c"], description = "Do not create the file if it does not exist.")
    internal var c: Boolean= false

    @Parameter(names = ["-a"], description = "Change the access time of the file.")
    internal var a: Boolean = false

    @Parameter(names = ["-m"], description = "Change the modification time of the file.")
    internal var m: Boolean = false

    @Parameter(names = ["-r"], description = "Use the access and modifications times from the specified file instead of the current time of day.")
    internal var r: String? = null

    @Parameter(names = ["-t"], description = "Change the access and modification times to the specified time instead of the current time of day.")
    internal var t: String? = null
}