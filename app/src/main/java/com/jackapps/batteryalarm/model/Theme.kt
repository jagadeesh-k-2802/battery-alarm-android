package com.jackapps.batteryalarm.model

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM_DEFAULT
}

fun Theme.format(): String {
    return when (this) {
        Theme.LIGHT -> "Light"
        Theme.DARK -> "Dark"
        Theme.SYSTEM_DEFAULT -> "System Default"
    }
}
