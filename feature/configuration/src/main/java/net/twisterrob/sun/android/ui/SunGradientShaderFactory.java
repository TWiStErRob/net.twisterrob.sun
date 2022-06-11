package net.twisterrob.sun.android.ui;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.graphics.drawable.ShapeDrawable;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

public class SunGradientShaderFactory extends ShapeDrawable.ShaderFactory {

	public enum Type {
		Radial,
		Vertical,
	}

	private static final @ColorInt int top = Color.rgb(0xAF, 0xD0, 0xE5); // light blue
	private static final @ColorInt int half = Color.rgb(0xFF, 165, 0x00); // orange
	private static final @ColorInt int horizon = Color.rgb(0xFF, 0xE1, 0x30); // orangish yellow
	private static final @ColorInt int duskC = Color.rgb(0xED, 0xC6, 0x8D); // darkish orange, almost browsn
	private static final @ColorInt int duskN = Color.rgb(0x88, 0x88, 0x99);
	private static final @ColorInt int duskA = Color.rgb(0x22, 0x22, 0x33);
	private static final @ColorInt int night = Color.rgb(0x00, 0x00, 0x00);
	private static final float m = 1;

	private final @NonNull Type type;
	@ColorInt
	private final @NonNull int[] colors;
	private final @NonNull float[] percentages;

	public SunGradientShaderFactory(@NonNull Type type) {
		this.type = type;
		switch (type) {
			case Radial: {
				this.colors = reverseInPlace(
						// day (top half circle, left to right)
						horizon, half, top, top, top, half, horizon,
						// night (bottom half circle, right to left)
						duskC, duskN, duskA, night, duskA, duskN, duskC,
						// back to horizon
						horizon
				);
				
				float[] degrees = {
						// day (top half circle, left to right)
						0 * m,
						3 * m,
						6 * m,
						90, // day peak
						180 - +6 * m,
						180 - +3 * m,
						180 - +0 * m,
						// night (bottom half circle, right to left)
						180 - -6 * m,
						180 - -12 * m,
						180 - -18 * m,
						180 - -90, // night peak
						360 + -18 * m,
						360 + -12 * m,
						360 + -6 * m,
						// back to horizon
						360 + 0 * m,
				};
				this.percentages = reverseInPlace(toPercentageInverseRadialInPlace(degrees));
				break;
			}
			case Vertical: {
				this.colors = new int[] {
						// night (bottom to top)
						night,
						duskA,
						duskN,
						duskC,
						// day (bottom to top)
						horizon,
						half,
						top, // to have a shorter gradient
						top,
				};
				
				float[] degrees = {
						// night (bottom to top)
						-90,
						-18 * m,
						-12 * m,
						-6 * m,
						// day (bottom to top)
						+0,
						+3 * m,
						+6 * m,
						90,
				};
				this.percentages = toPercentageInverseLinearInPlace(degrees);
				break;
			}
			default:
				throw new InternalError("Unsupported type: " + type);
		}
	}

	@Override
	public Shader resize(int width, int height) {
		switch (type) {
			case Radial:
				return new SweepGradient(width / 2f, height / 2f, colors, percentages);
			case Vertical:
				return new LinearGradient(width / 2f, height, width / 2f, 0, colors, percentages, TileMode.CLAMP);
			default:
				throw new InternalError("Unsupported type: " + type);
		}
	}

	/**
	 * {@link SweepGradient} needs the colors and positions in CW order, but math and sun works CCW.
	 * Also, it's easier to think in degrees instead of percentages, so that's converted too.
	 */
	private static @NonNull float[] toPercentageInverseRadialInPlace(
			@FloatRange(from = 0, to = 360) @NonNull float... fs
	) {
		for (int i = 0; i < fs.length; ++i) {
			fs[i] = 1 - fs[i] / 360f;
		}
		return fs;
	}

	private static @NonNull float[] toPercentageInverseLinearInPlace(
			@FloatRange(from = -90, to = +90) @NonNull float... fs
	) {
		for (int i = 0; i < fs.length; ++i) {
			fs[i] = (fs[i] + 90) / 180f;
		}
		return fs;
	}

	private static @NonNull float[] reverseInPlace(@NonNull float... a) {
		for (int i = 0; i < a.length / 2; ++i) {
			float tmp = a[i];
			a[i] = a[a.length - i - 1];
			a[a.length - i - 1] = tmp;
		}
		return a;
	}

	private static @NonNull int[] reverseInPlace(@NonNull int... a) {
		for (int i = 0; i < a.length / 2; ++i) {
			int tmp = a[i];
			a[i] = a[a.length - i - 1];
			a[a.length - i - 1] = tmp;
		}
		return a;
	}
}
