package net.twisterrob.sun.android.logic

import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

internal class SunAngleFormatter @Inject constructor() {

	private val time2: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
	private val time3: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
	private val fraction: DecimalFormat = (NumberFormat.getInstance() as DecimalFormat).apply {
		negativePrefix = ""
		negativeSuffix = ""
		positivePrefix = ""
		positiveSuffix = ""
		minimumIntegerDigits = 0
		maximumIntegerDigits = 0
		minimumFractionDigits = @Suppress("MagicNumber") 4
		maximumFractionDigits = @Suppress("MagicNumber") 4
	}

	fun formatFraction(angle: Double): Result {
		val sign = if (angle < 0) "-" else "" // Because I want to display ±0.
		return Result(
			angle = sign + abs(angle.toInt()),
			fraction = fraction.format(angle)
		)
	}

	fun formatTime2(time: Date): CharSequence =
		time2.format(time)

	fun formatTime3(time: Date): CharSequence =
		time3.format(time)

	internal class Result(
		@JvmField
		val angle: String,
		@JvmField
		val fraction: String
	)
}
