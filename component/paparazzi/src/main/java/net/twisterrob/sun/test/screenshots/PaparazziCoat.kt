package net.twisterrob.sun.test.screenshots

import android.content.Context
import android.view.View
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.RenderExtension
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class PaparazziCoat : TestRule {

	private val extensions: MutableSet<RenderExtension> = mutableSetOf()
	private val paparazzi: Paparazzi = createPaparazzi(extensions)

	override fun apply(base: Statement, description: Description): Statement =
		RuleChain
			.emptyRuleChain()
			.around(paparazzi)
			.around(PendingIntentBroadcast_ActivityManagerSingletonHack())
//			.around(AllowCreatingRemoteViewsHack(paparazzi::context)) // STOPSHIP
//			.around(ActivityTaskManagerSingletonHack()) // STOPSHIP
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

		fun createPaparazzi(extensions: Set<RenderExtension>): Paparazzi =
			Paparazzi(
				theme = "AppTheme.ScreenshotTest",
				maxPercentDifference = 0.0,
				renderExtensions = extensions,
				appCompatEnabled = false,
			)
	}
}
