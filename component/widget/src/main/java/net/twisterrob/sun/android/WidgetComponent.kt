package net.twisterrob.sun.android

import android.content.Context
import dagger.BindsInstance
import dagger.Component

@Component(
	modules = [
	]
)
interface WidgetComponent {

	fun inject(entry: SunAngleWidgetProvider)

	@Component.Factory
	interface Factory {

		fun create(
			@BindsInstance context: Context,
		): WidgetComponent
	}
}
