import net.twisterrob.gradle.doNotNagAbout

@Suppress("DSL_SCOPE_VIOLATION") // TODEL Gradle 7.x
plugins {
	id("project-module-root")
	alias(libs.plugins.android.app) apply false
	alias(libs.plugins.kotlin.android) apply false
	alias(libs.plugins.paparazzi) apply false
	alias(libs.plugins.twisterrob.root)
	alias(libs.plugins.twisterrob.quality)
	alias(libs.plugins.kotlin.detekt) apply false
}

buildscript {
	// Substitute for lack of settings.gradle's pluginManagement.resolutionStrategy.cacheChangingModulesFor.
	configurations.classpath.get().resolutionStrategy.cacheChangingModulesFor(0, "seconds") // -SNAPSHOT
}

tasks.register("check") {
	description = "Delegate task for checking included builds too."
	dependsOn(gradle.includedBuild("plugins").task(":check"))
}

val gradleVersion: String = GradleVersion.current().baseVersion.version

// region Gradle Sync warnings
// Currently with Gradle 7.5.1 only these 3 warnings show up.
// As of 2022 September Gradle 8 dev build still works with Android Studio 2021.3.1 (Dolphin).
// Tracking issue: https://youtrack.jetbrains.com/issue/IDEA-284158
// (parent https://youtrack.jetbrains.com/issue/IDEA-301941)
@Suppress("MaxLineLength")
doNotNagAbout(
	"Resolution of the configuration :component:core:provided was attempted from a context different than the project context. " +
		"Have a look at the documentation to understand why this is a problem and how it can be resolved. " +
		"This behaviour has been deprecated and is scheduled to be removed in Gradle 8.0. " +
		"See https://docs.gradle.org/${gradleVersion}/userguide/viewing_debugging_dependencies.html#sub:resolving-unsafe-configuration-resolution-errors for more details."
)
@Suppress("MaxLineLength")
doNotNagAbout(
	"Resolution of the configuration :component:lib:provided was attempted from a context different than the project context. " +
		"Have a look at the documentation to understand why this is a problem and how it can be resolved. " +
		"This behaviour has been deprecated and is scheduled to be removed in Gradle 8.0. " +
		"See https://docs.gradle.org/${gradleVersion}/userguide/viewing_debugging_dependencies.html#sub:resolving-unsafe-configuration-resolution-errors for more details."
)
@Suppress("MaxLineLength")
doNotNagAbout(
	"Resolution of the configuration :component:paparazzi:provided was attempted from a context different than the project context. " +
		"Have a look at the documentation to understand why this is a problem and how it can be resolved. " +
		"This behaviour has been deprecated and is scheduled to be removed in Gradle 8.0. " +
		"See https://docs.gradle.org/${gradleVersion}/userguide/viewing_debugging_dependencies.html#sub:resolving-unsafe-configuration-resolution-errors for more details."
)
// Android Studio 2022.1.1 (Electric Eel)
@Suppress("MaxLineLength")
doNotNagAbout(
	"The org.gradle.util.GUtil type has been deprecated. " +
		"This is scheduled to be removed in Gradle 9.0. " +
		"Consult the upgrading guide for further information: https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_7.html#org_gradle_util_reports_deprecations"
)
// endregion

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
