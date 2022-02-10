package net.twisterrob.sun.android;

import dagger.Component;

@Component
interface WidgetComponent {
	void inject(SunAngleWidgetProvider entry);
}
