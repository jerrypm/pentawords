package com.jeripurnama.pentaword.domain

enum class Language(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flagEmoji: String
) {
    INDONESIAN("id", "Indonesian", "Bahasa Indonesia", "\uD83C\uDDEE\uD83C\uDDE9"),
    MALAYSIAN("ms", "Malaysian", "Bahasa Melayu", "\uD83C\uDDF2\uD83C\uDDFE"),
    CHINESE("zh", "Chinese", "\u4E2D\u6587", "\uD83C\uDDE8\uD83C\uDDF3"),
    JAPANESE("ja", "Japanese", "\u65E5\u672C\u8A9E", "\uD83C\uDDEF\uD83C\uDDF5"),
    KOREAN("ko", "Korean", "\uD55C\uAD6D\uC5B4", "\uD83C\uDDF0\uD83C\uDDF7");

    companion object {
        fun fromCode(code: String): Language {
            return entries.find { it.code == code } ?: INDONESIAN
        }
    }
}
