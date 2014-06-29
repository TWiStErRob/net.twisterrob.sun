package net.twisterrob.android.sun.views;

import android.graphics.drawable.*;

public class RingSectionDrawable extends LayerDrawable {
	private RingDrawable selection;
	private RotatedDrawable selectionWrapper;

	public RingSectionDrawable(int innerRadius, int thickness) {
		super(new Drawable[]{new RotatedDrawable(new RingDrawable(innerRadius, thickness, 0, 0))});
		selectionWrapper = (RotatedDrawable)super.getDrawable(0);
		selection = (RingDrawable)selectionWrapper.getDrawable(0);
		selection.setUseLevelForShape(true);
	}

	public void setColor(int color) {
		selection.setColor(color);
	}

	public void setSize(int width, int height) {
		selection.setSize(width, height);
	}

	public void setSection(float angle, float sweep) {
		selectionWrapper.setAngle(180 + angle);

		float levelPercent = sweep / 360;
		selection.setLevel((int)(levelPercent * 10000));
	}
}
