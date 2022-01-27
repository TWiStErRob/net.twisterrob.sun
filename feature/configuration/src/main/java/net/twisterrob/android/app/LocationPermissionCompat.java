package net.twisterrob.android.app;

import java.util.Map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions;
import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

import net.twisterrob.android.app.LocationPermissionCompat.LocationPermissionEvents.RationaleContinuation;

/**
 * Abstract away the complexity of location permissions.
 * Goals:
 *  * clean, small interface
 *  * handle all API levels
 *  * be compliant and handle quirks
 *
 * Usage:
 *  * Create and store an instance in the Activity's constructor or onCreate.
 *  * Call {@link #executeWithPermissions()} from the action or {@link AppCompatActivity#onCreate(Bundle)}.
 */
public class LocationPermissionCompat {

	private final @NonNull AppCompatActivity activity;
	private final @NonNull LocationPermissionEvents callback;
	private final @NonNull ActivityResultLauncher<String[]> foregroundLocationPermissionLauncher;
	private final @NonNull ActivityResultLauncher<String[]> backgroundLocationPermissionLauncher;

	public LocationPermissionCompat(
			@NonNull AppCompatActivity activity,
			final @NonNull LocationPermissionEvents callback
	) {
		this.activity = activity;
		this.callback = callback;
		this.foregroundLocationPermissionLauncher = activity.registerForActivityResult(
				new RequestMultiplePermissions(),
				new ActivityResultCallback<Map<String, Boolean>>() {
					@Override public void onActivityResult(final Map<String, Boolean> isGranted) {
						if (isAllGranted(isGranted)) {
							requestBackground();
						} else {
							final String[] permissions = isGranted.keySet().toArray(new String[0]);
							if (needsAnyRationale(permissions)) {
								callback.showForegroundRationale(new RationaleContinuation() {
									@Override public void retry() {
										foregroundLocationPermissionLauncher.launch(permissions);
									}

									@Override public void cancel() {
										callback.failed();
									}
								});
							} else {
								callback.failed();
							}
						}
					}
				}
		);
		this.backgroundLocationPermissionLauncher = activity.registerForActivityResult(
				new RequestMultiplePermissions(),
				new ActivityResultCallback<Map<String, Boolean>>() {
					@Override public void onActivityResult(Map<String, Boolean> isGranted) {
						if (isAllGranted(isGranted)) {
							permissionsReady();
						} else {
							final String[] permissions = isGranted.keySet().toArray(new String[0]);
							if (needsAnyRationale(permissions)) {
								callback.showBackgroundRationale(new RationaleContinuation() {
									@Override public void retry() {
										backgroundLocationPermissionLauncher.launch(permissions);
									}

									@Override public void cancel() {
										callback.failed();
									}
								});
							} else {
								callback.failed();
							}
						}
					}
				}
		);
	}

	@AnyThread
	public @NonNull LocationState currentState() {
		if (!isLocationEnabled()) {
			return LocationState.LOCATION_DISABLED;
		}
		if (!hasCoarse()) {
			return LocationState.COARSE_DENIED;
		}
		if (!hasFine()) {
			return LocationState.FINE_DENIED;
		}
		if (!hasBackground()) {
			return LocationState.BACKGROUND_DENIED;
		}
		return LocationState.ALL_GRANTED;
	}

	@UiThread
	public void executeWithPermissions() {
		requestForeground();
	}

	private void permissionsReady() {
		callback.done();
	}

	private void requestForeground() {
		final String[] permissions = calculateForegroundPermissionsToRequest();
		if (hasAllPermissions(permissions)) {
			requestBackground();
		} else if (needsAnyRationale(permissions)) {
			callback.showForegroundRationale(new RationaleContinuation() {
				@Override public void retry() {
					foregroundLocationPermissionLauncher.launch(permissions);
				}

				@Override public void cancel() {
					callback.failed();
				}
			});
		} else {
			foregroundLocationPermissionLauncher.launch(permissions);
		}
	}

	private void requestBackground() {
		final String[] permissions = calculateBackgroundPermissionsToRequest();
		if (hasAllPermissions(permissions)) {
			permissionsReady();
		} else if (needsAnyRationale(permissions)) {
			callback.showBackgroundRationale(new RationaleContinuation() {
				@Override public void retry() {
					backgroundLocationPermissionLauncher.launch(permissions);
				}

				@Override public void cancel() {
					callback.failed();
				}
			});
		} else {
			backgroundLocationPermissionLauncher.launch(permissions);
		}
	}

	private boolean hasAllPermissions(@NonNull String... permissions) {
		for (String permission : permissions) {
			if (!hasPermission(permission)) {
				return false;
			}
		}
		return true;
	}

	private boolean hasPermission(@NonNull String permission) {
		return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
	}

	private boolean needsAnyRationale(@NonNull String... permissions) {
		for (String permission : permissions) {
			if (needsRationale(permission)) {
				return true;
			}
		}
		return false;
	}

	private boolean needsRationale(@NonNull String permission) {
		return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
	}

	private static boolean isAllGranted(@NonNull Map<String, Boolean> permissions) {
		for (Boolean isGranted : permissions.values()) {
			if (isGranted != Boolean.TRUE) {
				return false;
			}
		}
		return true;
	}

	@VisibleForTesting boolean isLocationEnabled() {
		LocationManager locationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
		return LocationManagerCompat.isLocationEnabled(locationManager);
	}

	@VisibleForTesting boolean hasCoarse() {
		return hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
	}

	@VisibleForTesting boolean hasFine() {
		return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
	}

	@VisibleForTesting boolean hasBackground() {
		if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
			return hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
		} else {
			return true;
		}
	}

	/**
	 * A simplified implementation of <a href="https://developer.android.com/training/location/permissions">Location Permissions</a> with reasons.
	 */
	@VisibleForTesting static @NonNull String[] calculateForegroundPermissionsToRequest() {
		String[] locationPermissions;
		if (Build.VERSION_CODES.R <= Build.VERSION.SDK_INT) {
			// Complex world. Background location need to be requested separately from FINE and COARSE.
			// > On Android 11 (API level 30) and higher, however, the system dialog doesn't include the "Allow all the time" option.
			// > Instead, users must enable background location on a settings page.
			// > If you try to request only ACCESS_FINE_LOCATION, the system ignores the request on some releases of Android 12.
			// > If your app targets Android 12 or higher, the system logs the following error message in Logcat:
			// > ACCESS_FINE_LOCATION must be requested with ACCESS_COARSE_LOCATION.
			// Even on Android 11 we get this:
			// > com.android.permissioncontroller E/GrantPermissionsActivity:
			// Apps targeting 30 must have foreground permission before requesting background and must request background on its own.
			locationPermissions = new String[] {
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION,
			};
		} else if (Build.VERSION_CODES.Q <= /*==*/ Build.VERSION.SDK_INT) {
			// Sad world, background location need to be requested too in order to update a widget.
			// > The system permissions dialog includes an option named "Allow all the time".
			locationPermissions = new String[] {
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION,
					// This is intentionally not included here,
					// as it's simpler to handle requesting location in two permission dialogs on all API levels.
					//Manifest.permission.ACCESS_BACKGROUND_LOCATION
			};
		} else /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)*/ {
			// Weird world, only FINE and COARSE to worry about, but to get FINE, one must include COARSE.
			// Android allows foreground and background for these.
			locationPermissions = new String[] {
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION,
			};
		}
		return locationPermissions;
	}

	@VisibleForTesting static @NonNull String[] calculateBackgroundPermissionsToRequest() {
		String[] locationPermissions;
		if (Build.VERSION_CODES.R <= Build.VERSION.SDK_INT) {
			locationPermissions = new String[] {
					Manifest.permission.ACCESS_BACKGROUND_LOCATION,
			};
		} else if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
			locationPermissions = new String[] {
					// This would normally be empty,
					// because on API 29 it is allowed to request all 3 permissions at once,
					// but to have less of a headache (e.g. having a 3rd rationale for the 3-at-once case),
					// I opted to handle 29 same as any future version (30, 31, ...).
					Manifest.permission.ACCESS_BACKGROUND_LOCATION,
			};
		} else /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)*/ {
			locationPermissions = new String[] {
					// None, because this permission doesn't exist yet (lint: InlinedApi).
					//Manifest.permission.ACCESS_BACKGROUND_LOCATION
			};
		}
		return locationPermissions;
	}

	@UiThread
	public interface LocationPermissionEvents {

		void done();

		void showForegroundRationale(@NonNull RationaleContinuation continuation);

		void showBackgroundRationale(@NonNull RationaleContinuation continuation);

		void failed();

		/**
		 * Callback for letting the user choose what to do after being shown a rationale.
		 */
		@UiThread
		interface RationaleContinuation {

			/**
			 * Call if the user chooses to continue, and wants to see the permission dialog again.
			 */
			void retry();

			/**
			 * Call if the user dismisses the UI showing the rationale.
			 */
			void cancel();
		}
	}

	public enum LocationState {
		LOCATION_DISABLED,
		COARSE_DENIED,
		FINE_DENIED,
		BACKGROUND_DENIED,
		ALL_GRANTED,
	}
}
