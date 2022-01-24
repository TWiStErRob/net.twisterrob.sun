package net.twisterrob.android.app;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoInteractions;

import android.Manifest;

import com.google.testing.junit.testparameterinjector.TestParameter;
import com.google.testing.junit.testparameterinjector.TestParameterInjector;

import static com.google.common.truth.Truth.assertThat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.twisterrob.android.app.LocationPermissionCompat.LocationPermissionEvents;
import net.twisterrob.android.app.LocationPermissionCompat.LocationState;

@RunWith(TestParameterInjector.class)
public class LocationPermissionCompatTest {

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

		for (LocationStateExpectations value : LocationStateExpectations.values()) {
			all.remove("" + value.hasLocation + value.hasCoarse + value.hasFine + value.hasBackground);
		}

		assertThat(all).isEmpty();
	}

	@Test public void testCurrentState(@TestParameter LocationStateExpectations test) {
		AppCompatActivity activity = mock(AppCompatActivity.class);
		LocationPermissionEvents callback = mock(LocationPermissionEvents.class);

		LocationPermissionCompat sut = spy(new LocationPermissionCompat(activity, callback));

		doReturn(test.hasLocation).when(sut).isLocationEnabled();
		doReturn(test.hasCoarse).when(sut).hasCoarse();
		doReturn(test.hasFine).when(sut).hasFine();
		doReturn(test.hasBackground).when(sut).hasBackground();

		LocationState actual = sut.currentState();

		assertThat(actual).isEqualTo(test.expectedState);
		verifyNoInteractions(callback);
	}

	@Test public void testForegroundPermissions(
			@TestParameter({
					"14", "15", "16", "17", "18", "19", "20",
					"21", "22", "23", "24", "25", "26", "27", "28",
					"29", "30", "31", "32"
			}) int version
	) {
		setAPIVersion(version);

		String[] foreground = LocationPermissionCompat.calculateForegroundPermissionsToRequest();

		assertThat(foreground)
				.asList()
				.containsExactly(
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.ACCESS_COARSE_LOCATION
				);
	}

	@Test public void testBackgroundPermissionsUnsupported(
			@TestParameter({
					"14", "15", "16", "17", "18", "19", "20",
					"21", "22", "23", "24", "25", "26", "27", "28"
			}) int version
	) {
		setAPIVersion(version);

		String[] foreground = LocationPermissionCompat.calculateBackgroundPermissionsToRequest();

		assertThat(foreground).isEmpty();
	}

	@Test public void testBackgroundPermissionsSeparately(
			@TestParameter({
					"29", "30", "31", "32"
			}) int version
	) {
		setAPIVersion(version);

		String[] foreground = LocationPermissionCompat.calculateBackgroundPermissionsToRequest();

		assertThat(foreground)
				.asList()
				.containsExactly(
						Manifest.permission.ACCESS_BACKGROUND_LOCATION
				);
	}

	private static void setAPIVersion(int version) {
		try {
			//noinspection JavaReflectionMemberAccess works on JVM, where it matters for Unit tests.
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			Field SDK_INT = android.os.Build.VERSION.class.getDeclaredField("SDK_INT");
			SDK_INT.setAccessible(true);
			modifiers.set(SDK_INT, SDK_INT.getModifiers() & ~Modifier.FINAL);
			SDK_INT.set(null, version);
		} catch (ReflectiveOperationException ex) {
			throw new IllegalStateException(ex);
		}
	}
}
