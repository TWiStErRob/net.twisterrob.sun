package net.twisterrob.android.app;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.Manifest;

import com.google.testing.junit.testparameterinjector.TestParameter;
import com.google.testing.junit.testparameterinjector.TestParameterInjector;

import static com.google.common.truth.Truth.assertThat;

@RunWith(TestParameterInjector.class)
public class LocationPermissionCompatTest {

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
