package net.twisterrob.android.app;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import android.app.Activity;

import com.google.testing.junit.testparameterinjector.TestParameter;
import com.google.testing.junit.testparameterinjector.TestParameterInjector;

import androidx.annotation.NonNull;

@RunWith(TestParameterInjector.class)
public class LocationStateDeterminerTest {

	private enum LocationStateExpectations {
		T0000000(LocationState.LOCATION_DISABLED, false, false, false, false, false, false, false),
		T0000001(LocationState.LOCATION_DISABLED, false, false, false, false, false, false, true),
		T0000010(LocationState.LOCATION_DISABLED, false, false, false, false, false, true, false),
		T0000011(LocationState.LOCATION_DISABLED, false, false, false, false, false, true, true),
		T0000100(LocationState.LOCATION_DISABLED, false, false, false, false, true, false, false),
		T0000101(LocationState.LOCATION_DISABLED, false, false, false, false, true, false, true),
		T0000110(LocationState.LOCATION_DISABLED, false, false, false, false, true, true, false),
		T0000111(LocationState.LOCATION_DISABLED, false, false, false, false, true, true, true),
		T0001000(LocationState.LOCATION_DISABLED, false, false, false, true, false, false, false),
		T0001001(LocationState.LOCATION_DISABLED, false, false, false, true, false, false, true),
		T0001010(LocationState.LOCATION_DISABLED, false, false, false, true, false, true, false),
		T0001011(LocationState.LOCATION_DISABLED, false, false, false, true, false, true, true),
		T0001100(LocationState.LOCATION_DISABLED, false, false, false, true, true, false, false),
		T0001101(LocationState.LOCATION_DISABLED, false, false, false, true, true, false, true),
		T0001110(LocationState.LOCATION_DISABLED, false, false, false, true, true, true, false),
		T0001111(LocationState.LOCATION_DISABLED, false, false, false, true, true, true, true),
		T0010000(LocationState.LOCATION_DISABLED, false, false, true, false, false, false, false),
		T0010001(LocationState.LOCATION_DISABLED, false, false, true, false, false, false, true),
		T0010010(LocationState.LOCATION_DISABLED, false, false, true, false, false, true, false),
		T0010011(LocationState.LOCATION_DISABLED, false, false, true, false, false, true, true),
		T0010100(LocationState.LOCATION_DISABLED, false, false, true, false, true, false, false),
		T0010101(LocationState.LOCATION_DISABLED, false, false, true, false, true, false, true),
		T0010110(LocationState.LOCATION_DISABLED, false, false, true, false, true, true, false),
		T0010111(LocationState.LOCATION_DISABLED, false, false, true, false, true, true, true),
		T0011000(LocationState.LOCATION_DISABLED, false, false, true, true, false, false, false),
		T0011001(LocationState.LOCATION_DISABLED, false, false, true, true, false, false, true),
		T0011010(LocationState.LOCATION_DISABLED, false, false, true, true, false, true, false),
		T0011011(LocationState.LOCATION_DISABLED, false, false, true, true, false, true, true),
		T0011100(LocationState.LOCATION_DISABLED, false, false, true, true, true, false, false),
		T0011101(LocationState.LOCATION_DISABLED, false, false, true, true, true, false, true),
		T0011110(LocationState.LOCATION_DISABLED, false, false, true, true, true, true, false),
		T0011111(LocationState.LOCATION_DISABLED, false, false, true, true, true, true, true),
		T0100000(LocationState.LOCATION_DISABLED, false, true, false, false, false, false, false),
		T0100001(LocationState.LOCATION_DISABLED, false, true, false, false, false, false, true),
		T0100010(LocationState.LOCATION_DISABLED, false, true, false, false, false, true, false),
		T0100011(LocationState.LOCATION_DISABLED, false, true, false, false, false, true, true),
		T0100100(LocationState.LOCATION_DISABLED, false, true, false, false, true, false, false),
		T0100101(LocationState.LOCATION_DISABLED, false, true, false, false, true, false, true),
		T0100110(LocationState.LOCATION_DISABLED, false, true, false, false, true, true, false),
		T0100111(LocationState.LOCATION_DISABLED, false, true, false, false, true, true, true),
		T0101000(LocationState.LOCATION_DISABLED, false, true, false, true, false, false, false),
		T0101001(LocationState.LOCATION_DISABLED, false, true, false, true, false, false, true),
		T0101010(LocationState.LOCATION_DISABLED, false, true, false, true, false, true, false),
		T0101011(LocationState.LOCATION_DISABLED, false, true, false, true, false, true, true),
		T0101100(LocationState.LOCATION_DISABLED, false, true, false, true, true, false, false),
		T0101101(LocationState.LOCATION_DISABLED, false, true, false, true, true, false, true),
		T0101110(LocationState.LOCATION_DISABLED, false, true, false, true, true, true, false),
		T0101111(LocationState.LOCATION_DISABLED, false, true, false, true, true, true, true),
		T0110000(LocationState.LOCATION_DISABLED, false, true, true, false, false, false, false),
		T0110001(LocationState.LOCATION_DISABLED, false, true, true, false, false, false, true),
		T0110010(LocationState.LOCATION_DISABLED, false, true, true, false, false, true, false),
		T0110011(LocationState.LOCATION_DISABLED, false, true, true, false, false, true, true),
		T0110100(LocationState.LOCATION_DISABLED, false, true, true, false, true, false, false),
		T0110101(LocationState.LOCATION_DISABLED, false, true, true, false, true, false, true),
		T0110110(LocationState.LOCATION_DISABLED, false, true, true, false, true, true, false),
		T0110111(LocationState.LOCATION_DISABLED, false, true, true, false, true, true, true),
		T0111000(LocationState.LOCATION_DISABLED, false, true, true, true, false, false, false),
		T0111001(LocationState.LOCATION_DISABLED, false, true, true, true, false, false, true),
		T0111010(LocationState.LOCATION_DISABLED, false, true, true, true, false, true, false),
		T0111011(LocationState.LOCATION_DISABLED, false, true, true, true, false, true, true),
		T0111100(LocationState.LOCATION_DISABLED, false, true, true, true, true, false, false),
		T0111101(LocationState.LOCATION_DISABLED, false, true, true, true, true, false, true),
		T0111110(LocationState.LOCATION_DISABLED, false, true, true, true, true, true, false),
		T0111111(LocationState.LOCATION_DISABLED, false, true, true, true, true, true, true),
		T1000000(LocationState.LOCATION_ENABLED, true, false, false, false, false, false, false),
		T1000001(LocationState.LOCATION_ENABLED, true, false, false, false, false, false, true),
		T1000010(LocationState.LOCATION_ENABLED, true, false, false, false, false, true, false),
		T1000011(LocationState.LOCATION_ENABLED, true, false, false, false, false, true, true),
		T1000100(LocationState.LOCATION_ENABLED, true, false, false, false, true, false, false),
		T1000101(LocationState.LOCATION_ENABLED, true, false, false, false, true, false, true),
		T1000110(LocationState.LOCATION_ENABLED, true, false, false, false, true, true, false),
		T1000111(LocationState.LOCATION_ENABLED, true, false, false, false, true, true, true),
		T1001000(LocationState.LOCATION_ENABLED, true, false, false, true, false, false, false),
		T1001001(LocationState.LOCATION_ENABLED, true, false, false, true, false, false, true),
		T1001010(LocationState.LOCATION_ENABLED, true, false, false, true, false, true, false),
		T1001011(LocationState.LOCATION_ENABLED, true, false, false, true, false, true, true),
		T1001100(LocationState.LOCATION_ENABLED, true, false, false, true, true, false, false),
		T1001101(LocationState.LOCATION_ENABLED, true, false, false, true, true, false, true),
		T1001110(LocationState.LOCATION_ENABLED, true, false, false, true, true, true, false),
		T1001111(LocationState.LOCATION_ENABLED, true, false, false, true, true, true, true),
		T1010000(LocationState.COARSE_DENIED, true, false, true, false, false, false, false),
		T1010001(LocationState.COARSE_DENIED, true, false, true, false, false, false, true),
		T1010010(LocationState.COARSE_DENIED, true, false, true, false, false, true, false),
		T1010011(LocationState.COARSE_DENIED, true, false, true, false, false, true, true),
		T1010100(LocationState.COARSE_DENIED, true, false, true, false, true, false, false),
		T1010101(LocationState.COARSE_DENIED, true, false, true, false, true, false, true),
		T1010110(LocationState.COARSE_DENIED, true, false, true, false, true, true, false),
		T1010111(LocationState.COARSE_DENIED, true, false, true, false, true, true, true),
		T1011000(LocationState.COARSE_DENIED, true, false, true, true, false, false, false),
		T1011001(LocationState.COARSE_DENIED, true, false, true, true, false, false, true),
		T1011010(LocationState.COARSE_DENIED, true, false, true, true, false, true, false),
		T1011011(LocationState.COARSE_DENIED, true, false, true, true, false, true, true),
		T1011100(LocationState.COARSE_DENIED, true, false, true, true, true, false, false),
		T1011101(LocationState.COARSE_DENIED, true, false, true, true, true, false, true),
		T1011110(LocationState.COARSE_DENIED, true, false, true, true, true, true, false),
		T1011111(LocationState.COARSE_DENIED, true, false, true, true, true, true, true),
		T1100000(LocationState.COARSE_GRANTED, true, true, false, false, false, false, false),
		T1100001(LocationState.COARSE_GRANTED, true, true, false, false, false, false, true),
		T1100010(LocationState.COARSE_GRANTED, true, true, false, false, false, true, false),
		T1100011(LocationState.COARSE_GRANTED, true, true, false, false, false, true, true),
		T1100100(LocationState.FINE_DENIED, true, true, false, false, true, false, false),
		T1100101(LocationState.FINE_DENIED, true, true, false, false, true, false, true),
		T1100110(LocationState.FINE_DENIED, true, true, false, false, true, true, false),
		T1100111(LocationState.FINE_DENIED, true, true, false, false, true, true, true),
		T1101000(LocationState.FINE_GRANTED, true, true, false, true, false, false, false),
		T1101001(LocationState.BACKGROUND_DENIED, true, true, false, true, false, false, true),
		T1101010(LocationState.BACKGROUND_GRANTED, true, true, false, true, false, true, false),
		T1101011(LocationState.BACKGROUND_GRANTED, true, true, false, true, false, true, true),
		T1101100(LocationState.FINE_GRANTED, true, true, false, true, true, false, false),
		T1101101(LocationState.BACKGROUND_DENIED, true, true, false, true, true, false, true),
		T1101110(LocationState.BACKGROUND_GRANTED, true, true, false, true, true, true, false),
		T1101111(LocationState.BACKGROUND_GRANTED, true, true, false, true, true, true, true),
		T1110000(LocationState.COARSE_GRANTED, true, true, true, false, false, false, false),
		T1110001(LocationState.COARSE_GRANTED, true, true, true, false, false, false, true),
		T1110010(LocationState.COARSE_GRANTED, true, true, true, false, false, true, false),
		T1110011(LocationState.COARSE_GRANTED, true, true, true, false, false, true, true),
		T1110100(LocationState.FINE_DENIED, true, true, true, false, true, false, false),
		T1110101(LocationState.FINE_DENIED, true, true, true, false, true, false, true),
		T1110110(LocationState.FINE_DENIED, true, true, true, false, true, true, false),
		T1110111(LocationState.FINE_DENIED, true, true, true, false, true, true, true),
		T1111000(LocationState.FINE_GRANTED, true, true, true, true, false, false, false),
		T1111001(LocationState.BACKGROUND_DENIED, true, true, true, true, false, false, true),
		T1111010(LocationState.BACKGROUND_GRANTED, true, true, true, true, false, true, false),
		T1111011(LocationState.BACKGROUND_GRANTED, true, true, true, true, false, true, true),
		T1111100(LocationState.FINE_GRANTED, true, true, true, true, true, false, false),
		T1111101(LocationState.BACKGROUND_DENIED, true, true, true, true, true, false, true),
		T1111110(LocationState.BACKGROUND_GRANTED, true, true, true, true, true, true, false),
		T1111111(LocationState.BACKGROUND_GRANTED, true, true, true, true, true, true, true),
		;

		final @NonNull LocationState expectedState;
		final boolean hasLocation;
		final boolean hasCoarse;
		final boolean shouldCoarseRationale;
		final boolean hasFine;
		final boolean shouldFineRationale;
		final boolean hasBackground;
		final boolean shouldBackgroundRationale;

		private LocationStateExpectations(
				@NonNull LocationState expectedState,
				boolean hasLocation,
				boolean hasCoarse, boolean shouldCoarseRationale,
				boolean hasFine, boolean shouldFineRationale,
				boolean hasBackground, boolean shouldBackgroundRationale
		) {
			this.expectedState = expectedState;

			this.hasLocation = hasLocation;

			this.hasCoarse = hasCoarse;
			this.shouldCoarseRationale = shouldCoarseRationale;

			this.hasFine = hasFine;
			this.shouldFineRationale = shouldFineRationale;

			this.hasBackground = hasBackground;
			this.shouldBackgroundRationale = shouldBackgroundRationale;
		}
	}

	@Test public void test(@TestParameter LocationStateExpectations test) {
		Activity activity = mock(Activity.class);

		LocationStateDeterminer sut = spy(new LocationStateDeterminer(activity));

		doReturn(test.hasLocation).when(sut).isLocationEnabled();
		doReturn(test.hasCoarse).when(sut).hasCoarse();
		doReturn(test.shouldCoarseRationale).when(sut).shouldCoarseRationale();
		doReturn(test.hasFine).when(sut).hasFine();
		doReturn(test.shouldFineRationale).when(sut).shouldFineRationale();
		doReturn(test.hasBackground).when(sut).hasBackground();
		doReturn(test.shouldBackgroundRationale).when(sut).shouldBackgroundRationale();

		LocationState actual = sut.determine(false);

		assertEquals(test.expectedState, actual);
	}
}
