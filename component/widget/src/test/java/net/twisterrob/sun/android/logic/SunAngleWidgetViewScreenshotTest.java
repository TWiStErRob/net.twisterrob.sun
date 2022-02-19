package net.twisterrob.sun.android.logic;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;

import com.google.testing.junit.testparameterinjector.TestParameter;
import com.google.testing.junit.testparameterinjector.TestParameterInjector;

import androidx.annotation.NonNull;

import net.twisterrob.sun.algo.SunSearchResults;
import net.twisterrob.sun.algo.SunSearchResults.Moment;
import net.twisterrob.sun.algo.SunSearchResults.Range;
import net.twisterrob.sun.algo.SunSearchResults.SunSearchParams;
import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;
import net.twisterrob.sun.test.screenshots.PaparazziCoat;
import net.twisterrob.sun.test.screenshots.ScreenshotTest;

import static net.twisterrob.sun.android.SunAngleWidgetPreferences.PREF_SHOW_PART_OF_DAY;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.PREF_SHOW_UPDATE_TIME;

@RunWith(TestParameterInjector.class)
@Category(ScreenshotTest.class)
public class SunAngleWidgetViewScreenshotTest {

	@Rule
	public PaparazziCoat paparazzi = new PaparazziCoat();

	private @NonNull SunAngleWidgetView sut;

	@Before
	public void setUp() {
		TimeProvider mockTime = mock(TimeProvider.class);
		when(mockTime.now()).thenReturn(midnight());
		sut = new SunAngleWidgetView(mockTime);
	}

	@Test
	public void testLightStateOnDevice(
			@TestParameter({"45", "0", "-3", "-6", "-9", "-12", "-15", "-18", "-21"}) float angle,
			@TestParameter Preset preset
	) {
		SunSearchResults results = createResult(angle, createEmptyParams());
		SharedPreferences prefs = mockPrefs(true, true);

		RemoteViews remoteViews = sut.createUpdateViews(paparazzi.getContext(), 0, results, prefs);

		snapshotWithSize(remoteViews.apply(paparazzi.getContext(), null), preset);
	}

	@Test
	public void testInvalid(
			@TestParameter Preset preset
	) {
		SharedPreferences prefs = mockPrefs(true, true);

		RemoteViews remoteViews = sut.createUpdateViews(paparazzi.getContext(), 0, null, prefs);

		snapshotWithSize(remoteViews.apply(paparazzi.getContext(), null), preset);
	}

	@Test
	public void testThresholdAndAngle(
			@TestParameter ThresholdRelation relation,
			@TestParameter({"0", "-12.3456789", "+12.3456789"}) float angle
	) {
		SunSearchResults results = createResult(
				angle,
				new SunSearchParams(
						Double.NaN,
						Double.NaN,
						midnight(),
						relation,
						angle
				)
		);
		SharedPreferences prefs = mockPrefs(true, true);

		RemoteViews remoteViews = sut.createUpdateViews(paparazzi.getContext(), 0, results, prefs);

		snapshotWithSize(remoteViews.apply(paparazzi.getContext(), null), Preset.Nice_Preview);
	}

	@Test
	public void testVisibility(
			@TestParameter boolean showPartOfDay,
			@TestParameter boolean showUpdateTime,
			@TestParameter Preset preset
	) {
		SunSearchResults results = createResult(Double.NaN, createEmptyParams());
		SharedPreferences prefs = mockPrefs(showPartOfDay, showUpdateTime);

		RemoteViews remoteViews = sut.createUpdateViews(paparazzi.getContext(), 0, results, prefs);

		snapshotWithSize(remoteViews.apply(paparazzi.getContext(), null), preset);
	}

	private static @NonNull SunSearchParams createEmptyParams() {
		return new SunSearchParams(
				Double.NaN,
				Double.NaN,
				midnight(),
				ThresholdRelation.ABOVE,
				Double.NaN
		);
	}

	private static @NonNull SunSearchResults createResult(double angle, SunSearchParams params) {
		return new SunSearchResults(
				params,
				new Moment(midnight(), angle),
				new Moment(midnight(), Double.NaN),
				new Moment(midnight(), Double.NaN),
				new Range(midnight(), midnight()),
				new Range(midnight(), midnight())
		);
	}

	private static @NonNull Calendar midnight() {
		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.HOUR_OF_DAY, 0);
		instance.set(Calendar.MINUTE, 0);
		instance.set(Calendar.SECOND, 0);
		instance.set(Calendar.MILLISECOND, 0);
		return instance;
	}

	private void snapshotWithSize(@NonNull View view, @NonNull Preset preset) {
		paparazzi.snapshotWithSize(view, preset.width, preset.height);
	}

	private static @NonNull SharedPreferences mockPrefs(boolean showPartOfDay, boolean showUpdateTime) {
		SharedPreferences prefs = mock(SharedPreferences.class);
		when(prefs.getBoolean(eq(PREF_SHOW_PART_OF_DAY), anyBoolean())).thenReturn(showPartOfDay);
		when(prefs.getBoolean(eq(PREF_SHOW_UPDATE_TIME), anyBoolean())).thenReturn(showUpdateTime);
		return prefs;
	}

	/**
	 * @see net.twisterrob.sun.android.preview.R.array.widget_size_preset
	 */
	@SuppressWarnings("JavadocReference")
	enum Preset {
		Nice_Preview("Nice Preview", 72, 86),
		Galaxy_S4("Galaxy S4", 84, 105),
		Galaxy_S3("Galaxy S3", 80, 100),
		Nexus_10("Nexus 10", 72, 72),
		Nexus_7_portrait("Nexus 7 (portrait)", 79.624f, 79.624f),
		Nexus_7_landscape("Nexus 7 (landscape)", 71.362f, 71.362f),
		;
		public final @NonNull String name;
		public final float width;
		public final float height;

		Preset(@NonNull String name, float width, float height) {
			this.name = name;
			this.width = width;
			this.height = height;
		}
	}
}
