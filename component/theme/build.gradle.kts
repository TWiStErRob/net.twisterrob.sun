plugins {
	id("project-module-android-library")
}

dependencies {
	implementation(projects.component.core)
	api(libs.androidx.appcompat)
}
android {
	namespace = "net.twisterrob.sun.android.theme"
}
