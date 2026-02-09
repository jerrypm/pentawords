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
    KOREAN("ko", "Korean", "\uD55C\uAD6D\uC5B4", "\uD83C\uDDF0\uD83C\uDDF7"),
    THAI("th", "Thai", "\u0E20\u0E32\u0E29\u0E32\u0E44\u0E17\u0E22", "\uD83C\uDDF9\uD83C\uDDED"),
    MYANMAR("my", "Myanmar", "\u1019\u103C\u1014\u103A\u1019\u102C\u1018\u102C\u101E\u102C", "\uD83C\uDDF2\uD83C\uDDF2"),
    VIETNAMESE("vi", "Vietnamese", "Ti\u1EBFng Vi\u1EC7t", "\uD83C\uDDFB\uD83C\uDDF3");

    companion object {
        fun fromCode(code: String): Language {
            return entries.find { it.code == code } ?: INDONESIAN
        }
    }
}
