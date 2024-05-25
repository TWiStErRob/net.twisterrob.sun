@file:JvmName("PaparazziFactory")

package net.twisterrob.sun.test.screenshots

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
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
	)
