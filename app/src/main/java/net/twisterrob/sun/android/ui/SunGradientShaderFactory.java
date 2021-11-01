package net.twisterrob.sun.android.ui;

import android.graphics.*;
import android.graphics.drawable.ShapeDrawable;

public class SunGradientShaderFactory extends ShapeDrawable.ShaderFactory {
	private static final int top = Color.rgb(0xAF, 0xD0, 0xE5); // light blue
	private static final int half = Color.rgb(0xFF, 165, 0x00); // orange
	private static final int horizon = Color.rgb(0xFF, 0xE1, 0x30); // orangish yellow
	private static final int duskC = Color.rgb(0xED, 0xC6, 0x8D); // darkish orange, almost browsn
	private static final int duskN = Color.rgb(0x88, 0x88, 0x99);
	private static final int duskA = Color.rgb(0x22, 0x22, 0x33);
	private static final int night = Color.rgb(0x00, 0x00, 0x00);
	private static final float m = 1;
	private static final int[] reverse = reverse( //
			horizon, half, top, top, top, half, horizon, // day
			duskC, duskN, duskA, night, duskA, duskN, duskC, // night
			horizon);
	private static final float[] percentage = reverse(toPercentageInverse( //
			0, 3 * m, 6 * m, 90, 180 - +6 * m, 180 - +3 * m, 180 - +0, // day
			180 - -6 * m, 180 - -12 * m, 180 - -18 * m, 180 - -90, 360 + -18 * m, 360 + -12 * m, 360 + -6 * m, // night
			360 + 0));
	@Override
	public Shader resize(int width, int height) {
		return new SweepGradient(width / 2, height / 2, reverse, percentage);
	}

	/**
	 * {@link SweepGradient} needs the colors and positions in CW order, but math and sun works CCW.
	 * Also it's easier to think in degrees instead of percentages, so that's converted too.
	 */
	private static float[] toPercentageInverse(float... fs) {
		for (int i = 0; i < fs.length; ++i) {
			fs[i] = 1 - fs[i] / 360f;
		}
		return fs;
	}
	private static float[] reverse(float... a) {
		for (int i = 0; i < a.length / 2; ++i) {
			float tmp = a[i];
			a[i] = a[a.length - i - 1];
			a[a.length - i - 1] = tmp;
		}
		return a;
	}
	private static int[] reverse(int... a) {
		for (int i = 0; i < a.length / 2; ++i) {
			int tmp = a[i];
			a[i] = a[a.length - i - 1];
			a[a.length - i - 1] = tmp;
		}
		return a;
	}
}