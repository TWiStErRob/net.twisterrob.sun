package net.twisterrob.sun.android.ui

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import net.twisterrob.sun.model.LightState
import net.twisterrob.sun.model.LightStateMap
import java.util.Calendar
import javax.inject.Inject

class UiStates @Inject constructor() {

	@DrawableRes
	fun getBackground(state: LightState, time: Calendar): Int =
		BGs[state, time]

	@StringRes
	fun getStateLabel(state: LightState, time: Calendar): Int =
		STATE_LABELs[state, time]

	@ColorRes
	fun getStateColor(state: LightState, time: Calendar): Int =
		STATE_COLORs[state, time]

	@ColorRes
	fun getUpdateColor(state: LightState, time: Calendar): Int =
		UPDATE_COLORs[state, time]

	@ColorRes
	fun getAngleColor(state: LightState, time: Calendar): Int =
		ANGLE_COLORs[state, time]

	companion object {

		private val STATE_LABELs: LightStateMap<Int> = StateNameIDs()
		private val BGs: LightStateMap<Int> = BackgroundIDs()
		private val ANGLE_COLORs: LightStateMap<Int> = AngleColorIDs()
		private val STATE_COLORs: LightStateMap<Int> = StateColorIDs()
		private val UPDATE_COLORs: LightStateMap<Int> = UpdateColorIDs()
	}
}
