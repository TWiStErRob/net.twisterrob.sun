package net.twisterrob.sun.android.logic

import java.util.Calendar
import javax.inject.Inject

class TimeProvider @Inject constructor() {

	fun now(): Calendar =
		Calendar.getInstance()
}
