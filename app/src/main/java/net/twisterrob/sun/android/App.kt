package net.twisterrob.sun.android

import android.app.Application

class App : Application() {

	override fun onCreate() {
		println(123456)
		super.onCreate()
	}
}
