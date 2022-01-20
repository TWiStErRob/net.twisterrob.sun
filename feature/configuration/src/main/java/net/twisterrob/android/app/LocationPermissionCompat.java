package net.twisterrob.android.app;

import java.util.Map;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

	private static final String TAG = "Config";

	private final @NonNull AppCompatActivity activity;
	private final @NonNull LocationPermissionEvents callback;
	private final @NonNull ActivityResultLauncher<String[]> foregroundLocationPermissionLauncher;
	private final @NonNull ActivityResultLauncher<String[]> backgroundLocationPermissionLauncher;

	public interface LocationPermissionEvents {

		void done();

		void showForegroundRationale(@NonNull RationaleContinuation continuation);

		void showBackgroundRationale(@NonNull RationaleContinuation continuation);

		void failed();

		/**
		 * Callback for letting the user choose what to do after being shown a rationale.
		 */
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
						Log.wtf(TAG, "foregroundResult: " + isGranted);
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
						Log.wtf(TAG, "backgroundResult: " + isGranted);
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

	private void permissionsReady() {
		callback.done();
	}

	public void executeWithPermissions() {
		requestForeground();
	}

	private void requestForeground() {
		final String[] permissions = LocationStateDeterminer.calculateForegroundPermissionsToRequest();
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
		final String[] permissions = LocationStateDeterminer.calculateBackgroundPermissionsToRequest();
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
}
