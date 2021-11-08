package net.twisterrob.sun.test.screenshots;

import java.awt.image.BufferedImage;

import android.view.View;

import androidx.annotation.NonNull;
import app.cash.paparazzi.RenderExtension;
import app.cash.paparazzi.Snapshot;

public class CropViewRenderExtension implements RenderExtension {

	@Override
	public @NonNull BufferedImage render(
			@NonNull Snapshot snapshot,
			@NonNull View view,
			@NonNull BufferedImage image
	) {
		return image.getSubimage(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
	}
}
