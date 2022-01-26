package net.twisterrob.android;

import java.util.Map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

public class PermissionInterrogator {

	public enum LocationState {
		LOCATION_DISABLED,
		COARSE_DENIED,
		FINE_DENIED,
		BACKGROUND_DENIED,
		ALL_GRANTED,
	}

	private final @NonNull Context context;

	public PermissionInterrogator(@NonNull Context context) {
		this.context = context;
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

	public boolean hasAllPermissions(@NonNull String... permissions) {
		for (String permission : permissions) {
			if (!hasPermission(permission)) {
				return false;
			}
		}
		return true;
	}

	public boolean hasPermission(@NonNull String permission) {
		return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
	}

	public boolean needsAnyRationale(@NonNull String... permissions) {
		for (String permission : permissions) {
			if (needsRationale(permission)) {
				return true;
			}
		}
		return false;
	}

	public boolean needsRationale(@NonNull String permission) {
		return ActivityCompat.shouldShowRequestPermissionRationale((Activity)context, permission);
	}

	public boolean isAllGranted(@NonNull Map<String, Boolean> permissions) {
		for (Boolean isGranted : permissions.values()) {
			if (isGranted != Boolean.TRUE) {
				return false;
			}
		}
		return true;
	}

	@VisibleForTesting boolean isLocationEnabled() {
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
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
}
