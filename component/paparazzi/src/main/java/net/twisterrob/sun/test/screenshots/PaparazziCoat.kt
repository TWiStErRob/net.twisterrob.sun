package net.twisterrob.sun.test.screenshots

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import androidx.annotation.Px
import app.cash.paparazzi.Paparazzi
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class PaparazziCoat : TestRule {

	private val paparazzi: Paparazzi = createPaparazzi()

	override fun apply(base: Statement, description: Description): Statement =
		RuleChain
			.emptyRuleChain()
			.around(paparazzi)
			.around(AllowCreatingRemoteViewsHack(paparazzi::context))
			.around(ActivityManagerSingletonHack())
			.around(ActivityTaskManagerSingletonHack())
			.apply(base, description)

	val context: Context
		get() = paparazzi.context

	fun snapshot(view: View) {
		paparazzi.snapshot(view)
	}

	fun snapshotWithSize(view: View, width: Float, height: Float) {
		val parent: ViewGroup = FrameLayout(view.context)
		val widthPx = dipToPix(view.context.resources, width)
		val heightPx = dipToPix(view.context.resources, height)
		parent.layoutParams = LayoutParams(widthPx.toInt(), heightPx.toInt())
		parent.addView(view)
		snapshot(parent)
	}

	@Px
	private fun dipToPix(resources: Resources, value: Float): Float =
		TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)

	companion object {

		fun createPaparazzi(): Paparazzi =
			Paparazzi(
				theme = "AppTheme.ScreenshotTest",
				maxPercentDifference = 0.0,
			)
	}
}
