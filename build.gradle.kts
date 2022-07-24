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

tasks.register<io.gitlab.arturbosch.detekt.report.ReportMergeTask>("detektReportMergeSarif") {
	output.set(rootProject.buildDir.resolve("reports/detekt/merge.sarif"))
}
tasks.register<io.gitlab.arturbosch.detekt.report.ReportMergeTask>("detektReportMergeXml") {
	output.set(rootProject.buildDir.resolve("reports/detekt/merge.xml"))
}

tasks.register("lintReportMergeSarif") {
	// Placeholder task for lifecycle hooking in CI. Will have dependencies from other projects.
}

tasks.register("check") {
	description = "Delegate task for checking included builds too."
	dependsOn(gradle.includedBuild("plugins").task(":check"))
}

// Ignore warning for https://issuetracker.google.com/issues/218478028 since Gradle 7.5,
// it's going to be fixed in AGP 7.3.
doNotNagAbout(
	"IncrementalTaskInputs has been deprecated. "
		+ "This is scheduled to be removed in Gradle 8.0. "
		+ "On method 'IncrementalTask.taskAction\$gradle_core' use 'org.gradle.work.InputChanges' instead. "
		+ "Consult the upgrading guide for further information: "
		+ "https://docs.gradle.org/7.5/userguide/upgrading_version_7.html#incremental_task_inputs_deprecation"
)
