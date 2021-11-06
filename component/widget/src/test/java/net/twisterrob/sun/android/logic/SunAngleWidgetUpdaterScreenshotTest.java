package net.twisterrob.sun.android.logic;

import java.util.Arrays;
import java.util.Calendar;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RemoteViews;

import net.twisterrob.sun.algo.SunSearchResults;
import net.twisterrob.sun.algo.SunSearchResults.Moment;
import net.twisterrob.sun.algo.SunSearchResults.Range;
import net.twisterrob.sun.algo.SunSearchResults.SunSearchParams;
import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;
import net.twisterrob.sun.test.screenshots.PaparazziCoat;

import static net.twisterrob.sun.android.SunAngleWidgetProvider.PREF_SHOW_PART_OF_DAY;
import static net.twisterrob.sun.android.SunAngleWidgetProvider.PREF_SHOW_UPDATE_TIME;

@RunWith(Parameterized.class)
public class SunAngleWidgetUpdaterScreenshotTest {

	private final @NonNull Preset preset;

	@Rule
	public PaparazziCoat paparazzi = new PaparazziCoat();

	public SunAngleWidgetUpdaterScreenshotTest(Preset preset) {
		this.preset = preset;
	}

	@Parameters(name = "{0}")
	public static Iterable<Object[]> parameters() {
		return Arrays.asList(
				new Object[]{new Preset("Nice Preview", 72, 86)},
				new Object[]{new Preset("Galaxy S4", 84, 105)},
				new Object[]{new Preset("Galaxy S3", 80, 100)},
				new Object[]{new Preset("Galaxy S2 (No margins)", 80, 100)},
				new Object[]{new Preset("Galaxy S2 (4 dp)", 72, 92)},
				new Object[]{new Preset("Galaxy S2 (8 dp)", 64, 84)},
				new Object[]{new Preset("Nexus 10", 72, 72)},
				new Object[]{new Preset("Nexus 7 (portrait)", (int)79.624, (int)79.624)},
				new Object[]{new Preset("Nexus 7 (landscape)", (int)71.362, (int)71.362)}
		);
	}

	@Test
	public void test() {
		SunAngleWidgetUpdater updater = new SunAngleWidgetUpdater(paparazzi.getContext());
		SunSearchResults results = new SunSearchResults(
				new SunSearchParams(
						Double.NaN,
						Double.NaN,
						Calendar.getInstance(),
						ThresholdRelation.ABOVE,
						Double.NaN
				),
				new Moment(Calendar.getInstance(), 12.3456789),
				new Moment(Calendar.getInstance(), -50),
				new Moment(Calendar.getInstance(), +40),
				new Range(Calendar.getInstance(), Calendar.getInstance()),
				new Range(Calendar.getInstance(), Calendar.getInstance())
		);
		RemoteViews remoteViews = updater.createUpdateViews(0, results, mockPrefs(true, true));
		snapshotWithSize(remoteViews.apply(paparazzi.getContext(), null), preset);
	}

	private void snapshotWithSize(@NonNull View view, Preset preset) {
		paparazzi.snapshotWithSize(view, preset.width, preset.height);
	}

	private static @NonNull SharedPreferences mockPrefs(boolean showPartOfDay, boolean showUpdateTime) {
		SharedPreferences prefs = mock(SharedPreferences.class);
		when(prefs.getBoolean(eq(PREF_SHOW_PART_OF_DAY), anyBoolean())).thenReturn(showPartOfDay);
		when(prefs.getBoolean(eq(PREF_SHOW_UPDATE_TIME), anyBoolean())).thenReturn(showUpdateTime);
		return prefs;
	}

	private static class Preset {

		public final @NonNull String name;
		public final float width;
		public final float height;

		public Preset(@NonNull String name, float width, float height) {
			this.name = name;
			this.width = width;
			this.height = height;
		}

		@Override public @NonNull String toString() {
			return String.format("%s (%f x %f)", name, width, height);
		}
	}
}
