plugins {
	id("project-module-android-library")
}

dependencies {
	implementation(projects.component.widget)
	implementation(projects.component.theme)
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.fragment)
}
android {
	namespace = "net.twisterrob.sun.preview"
}
