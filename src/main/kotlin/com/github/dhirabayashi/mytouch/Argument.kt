package com.github.dhirabayashi.mytouch

import com.beust.jcommander.Parameter

class Argument {
    @Parameter(description = "files")
    internal var files: List<String>? = null
}