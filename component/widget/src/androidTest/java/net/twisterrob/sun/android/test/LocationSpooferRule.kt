package net.twisterrob.sun.android.test

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.ExternalResource

class LocationSpooferRule(
	val spoofer: LocationSpoofer
) : ExternalResource() {

	constructor() : this(ApplicationProvider.getApplicationContext())
	constructor(context: Context) : this(LocationSpoofer(context))

	override fun before() {
		spoofer.initialize()
	}

	override fun after() {
		spoofer.cleanup()
	}
}
