package com.proger.cashtracker.ui.screens.pin

import android.widget.Toast
import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity

class PinCodeActivity : AppLockActivity() {

    // For Implementing forgot pin logic
    override fun showForgotDialog() {
//        Toast.makeText(this,"Implement your forgot password logic here.",Toast.LENGTH_LONG).show()
        Toast.makeText(this,"Sorry",Toast.LENGTH_LONG).show()
    }

    // For handling pin failure  events
    override fun onPinFailure(attempts: Int) {
        Toast.makeText(this,"Pin entered is Incorrect and attempts done are $attempts",Toast.LENGTH_LONG).show()
    }

    // For handling pin success events
    override fun onPinSuccess(attempts: Int) {
        Toast.makeText(this,"Correct Pin",Toast.LENGTH_LONG).show()
//        Toast.makeText(this,"Коректний пін-код",Toast.LENGTH_LONG).show()
    }

    // For overriding default length option
    // We can override the size of
    // Pin required as we want
    override fun getPinLength(): Int {
        return 4
    }
}