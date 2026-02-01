package net.twisterrob.android.app;

import java.util.Map;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions;
import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.annotation.VisibleForTesting;

import net.twisterrob.android.app.LocationPermissionCompat.LocationPermissionEvents.RationaleContinuation;
import net.twisterrob.android.app.PermissionInterrogator.LocationState;

/**
 * Abstract away the complexity of location permissions.
 * Goals:
 *  * clean, small interface
 *  * handle all API levels
 *  * be compliant and handle quirks
 *
 * Usage:
 *  * Create and store an instance in the Activity's constructor or onCreate.
 *  * Call {@link #executeWithPermissions()} from the action or {@link Activity#onCreate(Bundle)}.
 */
@SuppressWarnings("JavadocReference")
public class LocationPermissionCompat {

	private final @NonNull PermissionInterrogator interrogator;
	private final @NonNull LocationPermissionEvents callback;
	private final @NonNull ActivityResultLauncher<String[]> foregroundLocationPermissionLauncher;
	private final @NonNull ActivityResultLauncher<String[]> backgroundLocationPermissionLauncher;

	public LocationPermissionCompat(
			final @NonNull ActivityResultCaller activity,
			final @NonNull PermissionInterrogator interrogator,
			final @NonNull LocationPermissionEvents callback
	) {
		this.interrogator = interrogator;
		this.callback = callback;
		this.foregroundLocationPermissionLauncher = activity.registerForActivityResult(
				new RequestMultiplePermissions(),
				new ActivityResultCallback<>() {
					@Override public void onActivityResult(final Map<String, Boolean> isGranted) {
						if (interrogator.isAllGranted(isGranted)) {
							requestBackground();
						} else {
							final String[] permissions = isGranted.keySet().toArray(new String[0]);
							if (interrogator.needsAnyRationale(permissions)) {
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
				new ActivityResultCallback<>() {
					@Override public void onActivityResult(Map<String, Boolean> isGranted) {
						if (interrogator.isAllGranted(isGranted)) {
							permissionsReady();
						} else {
							final String[] permissions = isGranted.keySet().toArray(new String[0]);
							if (interrogator.needsAnyRationale(permissions)) {
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
		return interrogator.currentState();
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
		if (interrogator.hasAllPermissions(permissions)) {
			requestBackground();
		} else if (interrogator.needsAnyRationale(permissions)) {
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
		if (interrogator.hasAllPermissions(permissions)) {
			permissionsReady();
		} else if (interrogator.needsAnyRationale(permissions)) {
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
}
