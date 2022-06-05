package net.twisterrob.sun.test.screenshots

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import androidx.annotation.Dimension
import androidx.annotation.Px
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class PaparazziCoat(
	theme: String = "AppTheme.ScreenshotTest",
	appCompatEnabled: Boolean = true,
) : TestRule {

	private val paparazzi: Paparazzi = createPaparazzi(
		theme = theme,
		appCompatEnabled = appCompatEnabled,
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
		val widthPx = view.context.resources.dipToPix(width)
		val heightPx = view.context.resources.dipToPix(height)
		val parent = FrameLayout(view.context).apply {
			layoutParams = LayoutParams(widthPx.toInt(), heightPx.toInt())
			addView(view)
		}
		snapshot(parent)
	}

	companion object {

		private fun createPaparazzi(
			theme: String,
			appCompatEnabled: Boolean,
		): Paparazzi =
			Paparazzi(
				theme = theme,
				deviceConfig = DeviceConfig.PIXEL_2.copy(softButtons = false),
				maxPercentDifference = 0.0,
				appCompatEnabled = appCompatEnabled,
			)
	}
}

@Px
private fun Resources.dipToPix(
	@Dimension(unit = Dimension.DP) value: Float,
): Float =
	TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, this.displayMetrics)
