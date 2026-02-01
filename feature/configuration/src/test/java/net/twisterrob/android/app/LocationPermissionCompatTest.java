package net.twisterrob.android.app;

import static com.google.common.truth.Truth.assertThat;

import android.Manifest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
public class LocationPermissionCompatTest {

	@Test
	@Config(sdk = {/*14, ..*/ 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35})
	public void testForegroundPermissions() {
		String[] foreground = LocationPermissionCompat.calculateForegroundPermissionsToRequest();

		assertThat(foreground)
				.asList()
				.containsExactly(
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.ACCESS_COARSE_LOCATION
				);
	}

	@Test
	@Config(sdk = {/*14, ..*/ 23, 24, 25, 26, 27, 28})
	public void testBackgroundPermissionsUnsupported() {
		String[] background = LocationPermissionCompat.calculateBackgroundPermissionsToRequest();

		assertThat(background).isEmpty();
	}

	@Test
	@Config(sdk = {29, 30, 31, 32, 33, 34, 35})
	public void testBackgroundPermissionsSeparately() {
		String[] background = LocationPermissionCompat.calculateBackgroundPermissionsToRequest();
		assertThat(background)
				.asList()
				.containsExactly(
						Manifest.permission.ACCESS_BACKGROUND_LOCATION
				);
	}
}
