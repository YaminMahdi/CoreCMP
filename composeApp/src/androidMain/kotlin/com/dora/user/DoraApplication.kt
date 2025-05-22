package com.dora.user

import android.app.Application

class DoraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
//        Napier.base(DebugAntilog())
    }
}