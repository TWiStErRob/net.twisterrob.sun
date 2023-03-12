import net.twisterrob.gradle.doNotNagAbout

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

val gradleVersion: String = GradleVersion.current().version

// TODEL Gradle sync in AS EE 2022.1.1 https://youtrack.jetbrains.com/issue/IDEA-301430, fixed in AS Giraffe.
if (System.getProperty("idea.version") ?: "" < "2022.3") {
	@Suppress("MaxLineLength")
	doNotNagAbout(
		"The org.gradle.util.GUtil type has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_7.html#org_gradle_util_reports_deprecations",
		"at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl\$_getSourceSets_closure"
	)
} else {
	error("Android Studio version changed, please remove hack.")
}

// TODEL Gradle sync in AS EE 2022.1.1 https://youtrack.jetbrains.com/issue/IDEA-306975, maybe fixed in AS H.
@Suppress("MaxLineLength")
doNotNagAbout(
	"The AbstractArchiveTask.archivePath property has been deprecated. " +
		"This is scheduled to be removed in Gradle 9.0. " +
		"Please use the archiveFile property instead. " +
		"See https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath for more details.",
	"at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl\$_getSourceSets_closure"
)

// TODEL Gradle sync in AS EE 2022.1.1 https://youtrack.jetbrains.com/issue/IDEA-306975, maybe fixed in AS H.
@Suppress("MaxLineLength")
doNotNagAbout(
	"The AbstractArchiveTask.archivePath property has been deprecated. " +
		"This is scheduled to be removed in Gradle 9.0. " +
		"Please use the archiveFile property instead. " +
		"See https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath for more details.",
	"at org.jetbrains.plugins.gradle.tooling.util.SourceSetCachedFinder.createArtifactsMap"
)

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
