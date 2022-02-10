package net.twisterrob.sun.android

import dagger.Component

@Component
interface WidgetComponent {

	fun inject(entry: SunAngleWidgetProvider)
}
