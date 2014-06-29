package net.twisterrob.android.sun.views;

import android.graphics.Canvas;
import android.graphics.drawable.*;

public class RotatedDrawable extends LayerDrawable {
	private final Drawable drawable;
	private float angle;

	public RotatedDrawable(Drawable drawable) {
		this(drawable, 0);
	}

	public RotatedDrawable(Drawable drawable, float angle) {
		super(new Drawable[]{drawable});
		this.drawable = drawable;
		this.angle = angle;
	}

	@Override
	public void draw(final Canvas canvas) {
		canvas.save();
		canvas.rotate(angle, drawable.getBounds().width() / 2, drawable.getBounds().height() / 2);
		super.draw(canvas);
		canvas.restore();
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
		invalidateSelf();
	}
}
