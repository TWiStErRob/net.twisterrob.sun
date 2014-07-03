package net.twisterrob.android.sun.views;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.drawable.*;
import android.graphics.drawable.ShapeDrawable.ShaderFactory;

import net.twisterrob.android.sun.model.SunSearchResults.ThresholdRelation;
import net.twisterrob.android.sun.ui.SunGradientShaderFactory;

public class SunThresholdDrawable extends Drawable {
	private SunConstantState state;
	private boolean mutated;

	protected static final ShaderFactory factory = new SunGradientShaderFactory();

	protected Arc selected = new Arc();
	protected Arc minimum = new Arc();
	protected Arc maximum = new Arc();

	public SunThresholdDrawable() {
		this(new SunConstantState());
		selected.edges = true;
	}

	@Override
	public void draw(Canvas canvas) {
		drawBackground(canvas);
		minimum.draw(canvas);
		maximum.draw(canvas);
		selected.draw(canvas);
		//drawDebug(canvas);
	}

	protected void drawDebug(Canvas canvas) {
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		p.setStrokeWidth(1);
		p.setStyle(Style.STROKE);
		for (float r = 0; r < state.radius; r += 10) {
			canvas.drawCircle(state.radius, state.radius, r, p);
		}
	}

	protected void drawBackground(Canvas canvas) {
		int r = state.radius;
		canvas.drawCircle(r, r, r, state.sunPaint);
	}

	public void setSelectedVisuals(float gap, float thickness, int color) {
		selected.setVisuals(gap, thickness, color);
		invalidateSelf();
	}
	public void setSelectedEdge(boolean drawEdge) {
		selected.edges = drawEdge;
		invalidateSelf();
	}

	public void setMinimumVisuals(float gap, float thickness, int color) {
		minimum.setVisuals(gap, thickness, color);
		invalidateSelf();
	}
	public void setMinimumEdge(boolean drawEdge) {
		minimum.edges = drawEdge;
		invalidateSelf();
	}

	public void setMaximumVisuals(float gap, float thickness, int color) {
		maximum.setVisuals(gap, thickness, color);
		invalidateSelf();
	}
	public void setMaximumEdge(boolean drawEdge) {
		maximum.edges = drawEdge;
		invalidateSelf();
	}

	public void setRadius(int radius) {
		state.radius = radius;
		state.sunPaint.setShader(factory.resize(radius * 2, radius * 2));
		invalidateSelf();
	}

	public void setSelected(ThresholdRelation relation, float angle) {
		selected.setCurrent(relation, angle);
		invalidateSelf();
	}

	public void setMinMax(float min, float max) {
		minimum.setCurrent(ThresholdRelation.BELOW, min);
		maximum.setCurrent(ThresholdRelation.ABOVE, max);
		invalidateSelf();
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

	@Override
	public int getOpacity() {
		return 0;
	}
	@Override
	public int getIntrinsicWidth() {
		return state.radius * 2;
	}
	@Override
	public int getIntrinsicHeight() {
		return state.radius * 2;
	}

	@Override
	public Drawable mutate() {
		if (!mutated && super.mutate() == this) {
			initializeWithState(new SunConstantState(state));
			mutated = true;
		}
		return this;
	}

	private static final class SunConstantState extends ConstantState {
		public int changingConfigurations;
		public int radius;
		public Paint sunPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		public SunConstantState() {}
		public SunConstantState(SunConstantState state) {
			this.changingConfigurations = state.changingConfigurations;
			this.radius = state.radius;
			this.sunPaint = state.sunPaint;
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
	}

	private SunThresholdDrawable(SunConstantState state) {
		initializeWithState(state);
	}

	private void initializeWithState(SunConstantState state) {
		this.state = state;
		mutated = false;
	}

	@Override
	public int getChangingConfigurations() {
		return super.getChangingConfigurations() | state.changingConfigurations;
	}

	@Override
	public ConstantState getConstantState() {
		state.changingConfigurations = getChangingConfigurations();
		return state;
	}

	protected class Arc {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		float angle;
		float sweep;
		float gap;
		boolean edges;

		public Arc() {
			paint.setStyle(Style.STROKE);
			paint.setStrokeJoin(Join.ROUND);
		}

		public void draw(Canvas canvas) {
			float g/* ap */= gap;
			float t/* hickness */= paint.getStrokeWidth() / 2;
			float d/* iameter */= state.radius * 2;
			float c/* orrection */= g + t;
			canvas.drawArc(new RectF(c, c, d - c, d - c), angle, sweep, edges, paint);
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

		protected float calculateSweep(ThresholdRelation relation, float angle) {
			float sweep = -(angle + 90) * 2;
			if (relation == ThresholdRelation.BELOW) {
				sweep = sweep + 360;
			}
			return sweep;
		}
	}
}
