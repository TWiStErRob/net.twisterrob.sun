package net.twisterrob.sun.android.view;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Paint.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable.ShaderFactory;

import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;
import net.twisterrob.sun.android.ui.SunGradientShaderFactory;
import net.twisterrob.sun.android.ui.SunGradientShaderFactory.Type;

public class SunThresholdDrawable extends Drawable {
	private SunConstantState s;
	private boolean mutated;

	public SunThresholdDrawable() {
		this(new SunConstantState());
		s.selected.edges = true;
	}

	@Override
	public void draw(Canvas canvas) {
		drawBackground(canvas);
		s.minimum.draw(canvas);
		s.maximum.draw(canvas);
		s.selected.draw(canvas);
		//drawDebug(canvas);
	}

	protected void drawDebug(Canvas canvas) {
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		p.setStrokeWidth(1);
		p.setStyle(Style.STROKE);
		for (float r = 0; r < s.radius; r += 10) {
			canvas.drawCircle(s.radius, s.radius, r, p);
		}
	}

	protected void drawBackground(Canvas canvas) {
		int r = s.radius;
		canvas.drawCircle(r, r, r, s.sunPaint);
	}

	public void setSelectedVisuals(float gap, float thickness, int color) {
		s.selected.setVisuals(gap, thickness, color);
		invalidateSelf();
	}
	public void setSelectedEdge(boolean drawEdge) {
		s.selected.edges = drawEdge;
		invalidateSelf();
	}

	public void setMinimumVisuals(float gap, float thickness, int color) {
		s.minimum.setVisuals(gap, thickness, color);
		invalidateSelf();
	}
	public void setMinimumEdge(boolean drawEdge) {
		s.minimum.edges = drawEdge;
		invalidateSelf();
	}

	public void setMaximumVisuals(float gap, float thickness, int color) {
		s.maximum.setVisuals(gap, thickness, color);
		invalidateSelf();
	}
	public void setMaximumEdge(boolean drawEdge) {
		s.maximum.edges = drawEdge;
		invalidateSelf();
	}

	public void setRadius(int radius) {
		s.setRadius(radius);
		invalidateSelf();
	}

	public void setSelected(ThresholdRelation relation, float angle) {
		s.selected.setCurrent(relation, angle);
		invalidateSelf();
	}

	public void setMinMax(float min, float max) {
		s.minimum.setCurrent(ThresholdRelation.BELOW, min);
		s.maximum.setCurrent(ThresholdRelation.ABOVE, max);
		invalidateSelf();
	}

	@Override
	public void setAlpha(int alpha) {
		s.sunPaint.setAlpha(alpha);
		s.selected.paint.setAlpha(alpha);
		s.minimum.paint.setAlpha(alpha);
		s.maximum.paint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		s.sunPaint.setColorFilter(cf);
		s.selected.paint.setColorFilter(cf);
		s.minimum.paint.setColorFilter(cf);
		s.maximum.paint.setColorFilter(cf);
	}

	@Override
	@SuppressWarnings({"deprecation", "RedundantSuppression"}) // Not redundant, needed for javac.
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}
	@Override
	public int getIntrinsicWidth() {
		return s.radius * 2;
	}
	@Override
	public int getIntrinsicHeight() {
		return s.radius * 2;
	}

	@Override
	public Drawable mutate() {
		if (!mutated && super.mutate() == this) {
			initializeWithState(new SunConstantState(s));
			mutated = true;
		}
		return this;
	}

	private static final class SunConstantState extends ConstantState {
		private static final ShaderFactory SHADER_FACTORY = new SunGradientShaderFactory(Type.Radial);

		public int changingConfigurations;
		public int radius;
		public Paint sunPaint;
		public Arc selected;
		public Arc minimum;
		public Arc maximum;

		public SunConstantState() {
			sunPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			selected = new Arc();
			minimum = new Arc();
			maximum = new Arc();
		}
		public SunConstantState(SunConstantState state) {
			this.changingConfigurations = state.changingConfigurations;
			this.radius = state.radius;
			this.sunPaint = new Paint(state.sunPaint);
			this.selected = new Arc(state.selected);
			this.minimum = new Arc(state.minimum);
			this.maximum = new Arc(state.maximum);
		}

		@Override
		public Drawable newDrawable() {
			return new SunThresholdDrawable(this);
		}
		@Override
		public Drawable newDrawable(Resources res) {
			return new SunThresholdDrawable(this);
		}

		@Override
		public int getChangingConfigurations() {
			return changingConfigurations;
		}

		public void setRadius(int radius) {
			this.radius = radius;
			sunPaint.setShader(SHADER_FACTORY.resize(radius * 2, radius * 2));
		}

		private class Arc {
			public Paint paint;
			public float angle;
			public float sweep;
			public float gap;
			public boolean edges;
			private RectF tempBounds = new RectF();

			public Arc() {
				paint = new Paint(Paint.ANTI_ALIAS_FLAG);
				paint.setStyle(Style.STROKE);
				paint.setStrokeJoin(Join.ROUND);
			}

			public Arc(Arc other) {
				this.paint = new Paint(other.paint);
				this.angle = other.angle;
				this.sweep = other.sweep;
				this.gap = other.gap;
				this.edges = other.edges;
			}

			public void draw(Canvas canvas) {
				float g/* ap */ = gap;
				float t/* hickness */ = paint.getStrokeWidth() / 2;
				float d/* iameter */ = radius * 2;
				float c/* orrection */ = g + t; // half of the thickness is outside drawing bounds
				tempBounds.set(c, c, d - c, d - c);
				canvas.drawArc(tempBounds, angle, sweep, edges, paint);
			}

			public void setVisuals(float gap, float thickness, int color) {
				this.gap = gap;
				paint.setStrokeWidth(thickness);
				paint.setColor(color);
			}

			public void setCurrent(ThresholdRelation relation, float angle) {
				if (angle < -90 || 90 < angle) {
					throw new IllegalArgumentException("Angle (" + angle + ") must be in [-90, 90] range.");
				}
				this.angle = angle = -angle; // CW -> CCW
				this.sweep = calculateSweep(relation, angle);
			}

			private float calculateSweep(ThresholdRelation relation, float angle) {
				float sweep = -(angle + 90) * 2;
				if (relation == ThresholdRelation.BELOW) {
					sweep = sweep + 360;
				}
				return sweep;
			}
		}
	}

	private SunThresholdDrawable(SunConstantState state) {
		initializeWithState(state);
	}

	private void initializeWithState(SunConstantState state) {
		this.s = state;
		mutated = false;
	}

	@Override
	public int getChangingConfigurations() {
		return super.getChangingConfigurations() | s.changingConfigurations;
	}

	@Override
	public ConstantState getConstantState() {
		s.changingConfigurations = getChangingConfigurations();
		return s;
	}
}
