package net.twisterrob.sun.android;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class IntentOpener {

	private final @NonNull AppCompatActivity activity;

	public IntentOpener(@NonNull AppCompatActivity activity) {
		this.activity = activity;
	}

	public boolean canOpenAppSettings() {
		Intent intent = createAppSettingsIntent(activity.getPackageName());
		return intent.resolveActivity(activity.getPackageManager()) != null;
	}

	@GuardedBy("canOpenAppSettings")
	@SuppressWarnings("deprecation")
	public void openAppSettings() {
		// deprecation:Need to clean up code before I can change to registerForActivityResult.
		activity.startActivityForResult(createAppSettingsIntent(activity.getPackageName()), 0);
	}

	private static @NonNull Intent createAppSettingsIntent(@NonNull String packageName) {
		return new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
				.setData(Uri.fromParts("package", packageName, null))
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public boolean canOpenLocationSettings() {
		Intent intent = createLocationSettingsIntent();
		return intent.resolveActivity(activity.getPackageManager()) != null;
	}

	@GuardedBy("canOpenLocationSettings")
	public void openLocationSettings() {
		activity.startActivity(createLocationSettingsIntent());
	}

	private static @NonNull Intent createLocationSettingsIntent() {
		return new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	}

	public boolean canOpenAMapsApp() {
		Intent intent = createMapsIntent();
		return intent.resolveActivity(activity.getPackageManager()) != null;
	}

	@GuardedBy("canOpenAMapsApp")
	public void openAMapsApp() {
		activity.startActivity(createMapsIntent());
	}

	private static @NonNull Intent createMapsIntent() {
		return new Intent(Intent.ACTION_VIEW)
				// See https://developer.android.com/guide/components/intents-common#Maps
				.setData(Uri.parse("geo:0,0"))
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}
}
