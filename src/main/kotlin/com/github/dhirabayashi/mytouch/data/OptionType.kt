package com.github.dhirabayashi.mytouch.data

enum class OptionType {
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

    /**
     * 指定したファイルの日付を使用する
     */
    USE_TIMES_FROM_ANOTHER_FILE,

    /**
     * 指定した日付を使用する
     */
    USE_SPECIFIED_TIME,

    /**
     * タイムスタンプを補正する
     */
    ADJUST_TIME,
}
