@file:JvmName("PaparazziFactory")

package net.twisterrob.sun.test.screenshots

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Environment
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.detectEnvironment
import com.android.ide.common.rendering.api.SessionParams.RenderingMode

/**
 * Set up Paparazzi for widgets, components, and other small UI elements.
 */
fun widgetPaparazzi(): Paparazzi =
	Paparazzi(
		theme = "AppTheme.ScreenshotTest",
		maxPercentDifference = 0.0,
		appCompatEnabled = false,
		showSystemUi = false,
		renderingMode = RenderingMode.SHRINK,
		environment = env(),
	)

/**
 * Set up Paparazzi for Activity tests, which take the full screen (not fullscreen).
 */
fun activityPaparazzi(): Paparazzi =
	Paparazzi(
		theme = "AppTheme",
		deviceConfig = DeviceConfig.PIXEL_2,
		maxPercentDifference = 0.0,
		appCompatEnabled = true,
		showSystemUi = true,
		environment = env(),
	)

/**
 * See https://github.com/cashapp/paparazzi/issues/1025#issuecomment-1654065507.
 * TODO when changing this, update also `android.compileSdk` build.gradle.
 */
private fun env(): Environment =
	detectEnvironment().run {
		copy(
			compileSdkVersion = 33,
			platformDir = platformDir.replace("android-34", "android-33")
		)
	}
