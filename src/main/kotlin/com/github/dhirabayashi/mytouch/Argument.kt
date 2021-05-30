package com.github.dhirabayashi.mytouch

import com.beust.jcommander.Parameter

class Argument {
    @Parameter(description = "files")
    internal var files: List<String>? = null

    @Parameter(names = ["-c"], description = "Do not create the file if it does not exist.")
    internal var c: Boolean= false
}