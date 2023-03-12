@Suppress("DSL_SCOPE_VIOLATION") // TODEL https://github.com/gradle/gradle/issues/22797
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

// TODEL https://issuetracker.google.com/issues/247906487
if (com.android.Version.ANDROID_GRADLE_PLUGIN_VERSION.startsWith("7.")) {
	val loggerFactory: org.slf4j.ILoggerFactory = org.slf4j.LoggerFactory.getILoggerFactory()
	val addNoOpLogger: java.lang.reflect.Method = loggerFactory.javaClass
		.getDeclaredMethod("addNoOpLogger", String::class.java)
		.apply {
			isAccessible = true
		}
	addNoOpLogger(loggerFactory, "com.android.build.api.component.impl.MutableListBackedUpWithListProperty")
	addNoOpLogger(loggerFactory, "com.android.build.api.component.impl.MutableMapBackedUpWithMapProperty")
} else {
	error("AGP major version changed, review hack.")
}
