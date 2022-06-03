package net.twisterrob.sun.test.screenshots

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import androidx.annotation.Dimension
import androidx.annotation.Px
import app.cash.paparazzi.RenderExtension

class ConstrainedSizeRenderExtension(
	@Dimension(unit = Dimension.DP)
	private val width: Float,

	@Dimension(unit = Dimension.DP)
	private val height: Float,
) : RenderExtension {

	override fun renderView(contentView: View): View {
		val widthPx = dipToPix(contentView.context.resources, width)
		val heightPx = dipToPix(contentView.context.resources, height)
		return FrameLayout(contentView.context).apply {
			layoutParams = LayoutParams(widthPx.toInt(), heightPx.toInt())
			addView(contentView)
		}
	}

	companion object {

		@Px
		private fun dipToPix(
			resources: Resources,
			@Dimension(unit = Dimension.DP) value: Float,
		): Float =
			TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)
	}
}
