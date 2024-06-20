package com.proger.cashtracker

import android.app.Application
import com.github.orangegangsters.lollipin.lib.managers.LockManager
import com.proger.cashtracker.ui.screens.pin.PinCodeActivity
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        LockManager.getInstance().enableAppLock(this, PinCodeActivity::class.java)
        LockManager.getInstance().appLock.logoId = R.drawable.authorization
        LockManager.getInstance().appLock.timeout = 4000
        LockManager.getInstance().appLock.setShouldShowForgot(false)
    }
}