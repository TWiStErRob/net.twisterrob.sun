plugins {
	id("com.android.application") version "7.0.4" apply false
	// TODEL workaround in settings.gradle.kts once released.
	id("app.cash.paparazzi") version "0.9.0" apply false
	id("net.twisterrob.root") version "0.13"
	id("net.twisterrob.quality") version "0.13"
	id("project-dependencies") apply false
}

buildscript {
	// Substitute for lack of settings.gradle's pluginManagement.resolutionStrategy.cacheChangingModulesFor.
	configurations.classpath.get().resolutionStrategy.cacheChangingModulesFor(0, "seconds") // -SNAPSHOT
}
