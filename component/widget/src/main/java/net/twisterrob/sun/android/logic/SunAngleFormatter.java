package net.twisterrob.sun.android.logic;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.support.annotation.NonNull;

class SunAngleFormatter {

	private static final DecimalFormat FORMATTER = initFractionFormat();

	@NonNull Result format(double angle) {
		String sign = angle < 0? "-" : ""; // because I want to display ±0
		return new Result(
				sign + Math.abs((int)angle),
				FORMATTER.format(angle)
		);
	}

	private static @NonNull DecimalFormat initFractionFormat() {
		DecimalFormat nf = (DecimalFormat)NumberFormat.getInstance();
		nf.setNegativePrefix("");
		nf.setNegativeSuffix("");
		nf.setPositivePrefix("");
		nf.setPositiveSuffix("");
		nf.setMinimumIntegerDigits(0);
		nf.setMaximumIntegerDigits(0);
		nf.setMinimumFractionDigits(4);
		nf.setMaximumFractionDigits(4);
		return nf;
	}

	static class Result {

		public final @NonNull String angle;
		public final @NonNull String fraction;

		Result(@NonNull String angle, @NonNull String fraction) {
			this.angle = angle;
			this.fraction = fraction;
		}
	}
}
