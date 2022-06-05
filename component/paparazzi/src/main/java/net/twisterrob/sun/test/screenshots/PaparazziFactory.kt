@file:JvmName("PaparazziFactory")

package net.twisterrob.sun.test.screenshots

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi

fun widgetPaparazzi(): Paparazzi =
	Paparazzi(
		theme = "AppTheme.ScreenshotTest",
		deviceConfig = DeviceConfig.PIXEL_2.copy(softButtons = false),
		maxPercentDifference = 0.0,
		appCompatEnabled = false
	)

fun activityPaparazzi(): Paparazzi =
	Paparazzi(
		theme = "AppTheme",
		deviceConfig = DeviceConfig.PIXEL_2,
		maxPercentDifference = 0.0,
		appCompatEnabled = true
	)
