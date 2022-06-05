package net.twisterrob.sun.android;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.google.testing.junit.testparameterinjector.TestParameterInjector;

import androidx.annotation.NonNull;

import net.twisterrob.sun.algo.SunSearchResults;
import net.twisterrob.sun.algo.SunSearchResults.Moment;
import net.twisterrob.sun.algo.SunSearchResults.Range;
import net.twisterrob.sun.algo.SunSearchResults.SunSearchParams;
import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;
import net.twisterrob.sun.configuration.R;
import net.twisterrob.sun.test.screenshots.PaparazziCoat;
import net.twisterrob.sun.test.screenshots.PaparazziFactory;
import net.twisterrob.sun.test.screenshots.ScreenshotTest;
import net.twisterrob.sun.test.screenshots.UsableActivityHackKt;

@RunWith(TestParameterInjector.class)
@Category(ScreenshotTest.class)
public class SunAngleWidgetConfigurationScreenshotTest {

	@Rule
	public PaparazziCoat paparazzi = new PaparazziCoat(PaparazziFactory.activityPaparazzi());

	private @NonNull SunAngleWidgetConfiguration sut;

	@Before
	public void setUp() {
		sut = new SunAngleWidgetConfiguration();
		UsableActivityHackKt.start(
				sut,
				paparazzi.getContext(),
				new Intent(paparazzi.getContext(), SunAngleWidgetConfiguration.class)
		);

		// Set up LocationManager to be enabled so the UI doesn't show the red banner.
		LocationManager service = (LocationManager)sut.getSystemService(Context.LOCATION_SERVICE);
		Mockito.doReturn(true).when(service).isLocationEnabled();

		// Inflate the views.
		sut.onCreate(null, null);
		sut.onPostCreate(null, null);
	}

	private @NonNull View detachView() {
		View view = sut.getWindow().getDecorView().findViewById(android.R.id.content);
		((ViewGroup)view.getParent()).removeView(view);
		return view;
	}

	@Test
	public void testNoLocation() {
		sut.updateUI(createResults(Double.NaN, Double.NaN));

		paparazzi.snapshot(detachView());
	}

	@Test
	public void testValidSettings() {
		sut.updateUI(createResults(0, 0));

		paparazzi.snapshot(detachView());
	}

	@Test
	public void testTooHigh() {
		((SeekBar)sut.findViewById(R.id.angle)).setProgress(90 + 60);

		sut.updateUI(createResults(0, 0));

		paparazzi.snapshot(detachView());
	}

	@Test
	public void testTooLow() {
		((SeekBar)sut.findViewById(R.id.angle)).setProgress(90 - 60);

		sut.updateUI(createResults(0, 0));

		paparazzi.snapshot(detachView());
	}

	private @NonNull SunSearchResults createResults(double latitude, double longitude) {
		return new SunSearchResults(
				new SunSearchParams(
						latitude,
						longitude,
						midnight(),
						ThresholdRelation.ABOVE,
						Double.NaN
				),
				new Moment(midnight(), Double.NaN),
				new Moment(midnight(), -45),
				new Moment(midnight(), +45),
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
}
