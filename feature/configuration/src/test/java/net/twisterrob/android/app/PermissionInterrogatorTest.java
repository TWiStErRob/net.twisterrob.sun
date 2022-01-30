package net.twisterrob.android.app;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoInteractions;

import android.content.Context;

import com.google.testing.junit.testparameterinjector.TestParameter;
import com.google.testing.junit.testparameterinjector.TestParameterInjector;

import static com.google.common.truth.Truth.assertThat;

import androidx.annotation.NonNull;

import net.twisterrob.android.app.PermissionInterrogator.LocationState;

@RunWith(TestParameterInjector.class)
public class PermissionInterrogatorTest {

	private enum LocationStateExpectations {
		T0000(LocationState.LOCATION_DISABLED, false, false, false, false),
		T0001(LocationState.LOCATION_DISABLED, false, false, false, true),
		T0010(LocationState.LOCATION_DISABLED, false, false, true, false),
		T0011(LocationState.LOCATION_DISABLED, false, false, true, true),
		T0100(LocationState.LOCATION_DISABLED, false, true, false, false),
		T0101(LocationState.LOCATION_DISABLED, false, true, false, true),
		T0110(LocationState.LOCATION_DISABLED, false, true, true, false),
		T0111(LocationState.LOCATION_DISABLED, false, true, true, true),
		T1000(LocationState.COARSE_DENIED, true, false, false, false),
		T1001(LocationState.COARSE_DENIED, true, false, false, true),
		T1010(LocationState.COARSE_DENIED, true, false, true, false),
		T1011(LocationState.COARSE_DENIED, true, false, true, true),
		T1100(LocationState.FINE_DENIED, true, true, false, false),
		T1101(LocationState.FINE_DENIED, true, true, false, true),
		T1110(LocationState.BACKGROUND_DENIED, true, true, true, false),
		T1111(LocationState.ALL_GRANTED, true, true, true, true),
		;

		final @NonNull LocationState expectedState;
		final boolean hasLocation;
		final boolean hasCoarse;
		final boolean hasFine;
		final boolean hasBackground;

		LocationStateExpectations(
				@NonNull LocationState expectedState,
				boolean hasLocation,
				boolean hasCoarse,
				boolean hasFine,
				boolean hasBackground
		) {
			this.expectedState = expectedState;
			this.hasLocation = hasLocation;
			this.hasCoarse = hasCoarse;
			this.hasFine = hasFine;
			this.hasBackground = hasBackground;
		}
	}

	@Test public void testCurrentStateCoverage() {
		boolean[] values = {true, false};
		Collection<String> all = new HashSet<>();
		for (boolean hasL : values) {
			for (boolean hasC : values) {
				for (boolean hasF : values) {
					for (boolean hasB : values) {
						all.add("" + hasL + hasC + hasF + hasB);
					}
				}
			}
		}

		Collection<String> covered = new HashSet<>();
		for (LocationStateExpectations value : LocationStateExpectations.values()) {
			covered.add("" + value.hasLocation + value.hasCoarse + value.hasFine + value.hasBackground);
		}

		assertThat(covered).containsExactlyElementsIn(all);
	}

	@Test public void testCurrentState(@TestParameter LocationStateExpectations test) {
		Context context = mock(Context.class);

		PermissionInterrogator sut = spy(new PermissionInterrogator(context));

		doReturn(test.hasLocation).when(sut).isLocationEnabled();
		doReturn(test.hasCoarse).when(sut).hasCoarse();
		doReturn(test.hasFine).when(sut).hasFine();
		doReturn(test.hasBackground).when(sut).hasBackground();

		LocationState actual = sut.currentState();

		assertThat(actual).isEqualTo(test.expectedState);
		verifyNoInteractions(context);
	}
}
