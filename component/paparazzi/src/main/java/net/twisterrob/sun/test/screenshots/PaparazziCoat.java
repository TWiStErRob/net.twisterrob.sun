package net.twisterrob.sun.test.screenshots;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.Callable;

import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import app.cash.paparazzi.DeviceConfig;
import app.cash.paparazzi.EnvironmentKt;
import app.cash.paparazzi.HtmlReportWriter;
import app.cash.paparazzi.Paparazzi;
import app.cash.paparazzi.SnapshotVerifier;

public class PaparazziCoat implements TestRule {

	private final @NonNull Paparazzi paparazzi;

	public PaparazziCoat() {
		this.paparazzi = PaparazziCoat.createPaparazzi();
	}

	@Override public @NonNull Statement apply(@NonNull Statement base, @NonNull Description description) {
		return RuleChain
				.emptyRuleChain()
				.around(paparazzi)
				.around(new AllowCreatingRemoteViewsHack(new Callable<Context>() {
					@Override public Context call() {
						return paparazzi.getContext();
					}
				}))
				.around(new ActivityManagerSingletonHack())
				.around(new ActivityTaskManagerSingletonHack())
				.apply(base, description);
	}

	public @NonNull Context getContext() {
		return paparazzi.getContext();
	}

	public void snapshot(@NonNull View view) {
		paparazzi.snapshot(view, null, null, null);
	}

	public void snapshotWithSize(@NonNull View view, float width, float height) {
		ViewGroup parent = new FrameLayout(view.getContext());
		float widthPx = dipToPix(view.getContext().getResources(), width);
		float heightPx = dipToPix(view.getContext().getResources(), height);
		parent.setLayoutParams(new LayoutParams((int)widthPx, (int)heightPx));
		parent.addView(view);
		snapshot(parent);
	}

	private float dipToPix(Resources resources, float value) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.getDisplayMetrics());
	}

	public static @NonNull Paparazzi createPaparazzi() {
		return new Paparazzi(
				EnvironmentKt.detectEnvironment(),
				DeviceConfig.Companion.getNEXUS_5(),
				"android:Theme.Material.NoActionBar.Fullscreen",
				true,
				0.0,
				Boolean.parseBoolean(System.getProperty("paparazzi.test.verify"))
						? new SnapshotVerifier(0.0, new File("src/test/snapshots"))
						: new HtmlReportWriter(),
				new HashSet<>(Collections.singletonList(new CropViewRenderExtension()))
		);
	}
}
