package com.dora.user.data.local

import com.russhwolf.settings.Settings

object LocalSettings {
    val settings by lazy { Settings() }
    var isUserLoggedIn
        get() = settings.getBoolean("isUserLoggedIn", false)
        set(value) = settings.putBoolean("isUserLoggedIn", value)

    fun clear() {
        settings.clear()
    }

}