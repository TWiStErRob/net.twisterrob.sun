plugins {
	id("project-module-root")
	alias(libs.plugins.android.app) apply false
	alias(libs.plugins.kotlin.android) apply false
	alias(libs.plugins.paparazzi) apply false
	alias(libs.plugins.twisterrob.root)
	alias(libs.plugins.twisterrob.quality)
	alias(libs.plugins.kotlin.detekt) apply false
}

tasks.register("check") {
	description = "Delegate task for checking included builds too."
	dependsOn(gradle.includedBuild("plugins").task(":check"))
}
