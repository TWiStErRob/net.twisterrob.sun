package net.twisterrob.sun.android

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import net.twisterrob.sun.algo.SunCalculator
import net.twisterrob.sun.pveducation.PhotovoltaicSun

@Component(
	modules = [
		WidgetComponent.Provisions::class,
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

	@Module
	object Provisions {

		@Provides
		fun provideSun(): SunCalculator =
			SunCalculator(PhotovoltaicSun())
	}
}
