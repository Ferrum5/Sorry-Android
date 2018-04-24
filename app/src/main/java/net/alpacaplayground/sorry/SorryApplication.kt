package net.alpacaplayground.sorry

import android.app.Application

class SorryApplication : Application(){
    companion object {
        lateinit var application: SorryApplication
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}