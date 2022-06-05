package net.twisterrob.sun.test.screenshots

import android.content.Context
import android.view.View
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.RenderExtension
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class PaparazziCoat(
	theme: String = "AppTheme.ScreenshotTest",
	appCompatEnabled: Boolean = true,
) : TestRule {

	private val extensions: MutableSet<RenderExtension> = mutableSetOf()
	private val paparazzi: Paparazzi = createPaparazzi(
		theme = theme,
		appCompatEnabled = appCompatEnabled,
		extensions = extensions
	)

	override fun apply(base: Statement, description: Description): Statement =
		RuleChain
			.emptyRuleChain()
			.around(paparazzi)
			.around(ActivityManagerSingletonHack())
			.around(ActivityClientSingletonHack())
			.around(ActivityTaskManagerSingletonHack())
			.apply(base, description)

	val context: Context
		get() = paparazzi.context

	fun snapshot(view: View) {
		paparazzi.snapshot(view)
	}

	fun snapshotWithSize(view: View, width: Float, height: Float) {
		val extension = ConstrainedSizeRenderExtension(width, height)
		try {
			extensions.add(extension)
			snapshot(view)
		} finally {
			extensions.remove(extension)
		}
	}

	companion object {

		private fun createPaparazzi(
			theme: String,
			appCompatEnabled: Boolean,
			extensions: Set<RenderExtension>
		): Paparazzi =
			Paparazzi(
				theme = theme,
				deviceConfig = DeviceConfig.PIXEL_2.copy(softButtons = false),
				maxPercentDifference = 0.0,
				renderExtensions = extensions,
				appCompatEnabled = appCompatEnabled,
			)
	}
}
