package net.twisterrob.sun.android.logic

import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.abs

internal class SunAngleFormatter {

	fun format(angle: Double): Result {
		val sign = if (angle < 0) "-" else "" // Because I want to display ±0.
		return Result(
			angle = sign + abs(angle.toInt()),
			fraction = FORMATTER.format(angle)
		)
	}

	internal class Result(
		val angle: String,
		val fraction: String
	)

	companion object {

		private val FORMATTER: DecimalFormat = (NumberFormat.getInstance() as DecimalFormat).apply {
			negativePrefix = ""
			negativeSuffix = ""
			positivePrefix = ""
			positiveSuffix = ""
			minimumIntegerDigits = 0
			maximumIntegerDigits = 0
			minimumFractionDigits = 4
			maximumFractionDigits = 4
		}
	}
}
