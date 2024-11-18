package com.nizarmah.igatha

import android.app.Application

class IgathaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        UserSettings.init(this)
    }
}
