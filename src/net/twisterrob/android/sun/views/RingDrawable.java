package net.twisterrob.android.sun.views;

import java.lang.reflect.Field;

import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;

public class RingDrawable extends GradientDrawable {
	private final Class<?> mGradientState = resolveGradientState();
	private final Field innerRadius = resolveField(mGradientState, "mInnerRadius");
	private final Field thickness = resolveField(mGradientState, "mThickness");
	private final Field innerRadiusRatio = resolveField(mGradientState, "mInnerRadiusRatio");
	private final Field thicknessRatio = resolveField(mGradientState, "mThicknessRatio");
	private final Field useLevelForShape = resolveField(mGradientState, "mUseLevelForShape");

	public RingDrawable() {
		this(Orientation.TOP_BOTTOM, null);
	}

	public RingDrawable(int innerRadius, int thickness, float innerRadiusRatio, float thicknessRatio) {
		this(Orientation.TOP_BOTTOM, null, innerRadius, thickness, innerRadiusRatio, thicknessRatio);
	}

	public RingDrawable(GradientDrawable.Orientation orientation, int[] colors, int innerRadius, int thickness,
			float innerRadiusRatio, float thicknessRatio) {
		this(orientation, colors);
		setInnerRadius(innerRadius);
		setThickness(thickness);
		setInnerRadiusRatio(innerRadiusRatio);
		setThicknessRatio(thicknessRatio);
	}

	public RingDrawable(GradientDrawable.Orientation orientation, int[] colors) {
		super(orientation, colors);
		setShape(RING);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}

	public void setInnerRadius(int radius) {
		try {
			innerRadius.setInt(getConstantState(), radius);
		} catch (Exception ex) {
			throw new IllegalStateException(GradientDrawable.class + " has inconsistent implementation", ex);
		}
	}

	public void setThickness(int thicknessValue) {
		try {
			thickness.setInt(getConstantState(), thicknessValue);
		} catch (Exception ex) {
			throw new IllegalStateException(GradientDrawable.class + " has inconsistent implementation", ex);
		}
	}

	public void setInnerRadiusRatio(float ratio) {
		try {
			innerRadiusRatio.setFloat(getConstantState(), ratio);
		} catch (Exception ex) {
			throw new IllegalStateException(GradientDrawable.class + " has inconsistent implementation", ex);
		}
	}

	public void setThicknessRatio(float ratio) {
		try {
			thicknessRatio.setFloat(getConstantState(), ratio);
		} catch (Exception ex) {
			throw new IllegalStateException(GradientDrawable.class + " has inconsistent implementation", ex);
		}
	}

	public void setUseLevelForShape(boolean useLevel) {
		try {
			useLevelForShape.setBoolean(getConstantState(), useLevel);
		} catch (Exception ex) {
			throw new IllegalStateException(GradientDrawable.class + " has inconsistent implementation", ex);
		}
	}

	private static Class<?> resolveGradientState() {
		Class<?>[] classes = GradientDrawable.class.getDeclaredClasses();
		for (Class<?> singleClass: classes) {
			if (singleClass.getSimpleName().equals("GradientState")) {
				return singleClass;
			}
		}
		throw new RuntimeException("GradientState could not be found in current GradientDrawable implementation");
	}

	private static Field resolveField(Class<?> source, String fieldName) {
		try {
			Field field = source.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field;
		} catch (Exception ex) {
			throw new IllegalStateException(GradientDrawable.class + " has inconsistent implementation", ex);
		}
	}
}