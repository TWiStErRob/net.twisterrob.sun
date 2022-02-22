plugins {
	// TODEL workaround in :app/build.gradle.kts for 194525628 for 7.2+
	id("com.android.application") version "7.0.4" apply false
	id("org.jetbrains.kotlin.android") version "1.5.31" apply false
	// TODEL workaround in settings.gradle.kts once released.
	id("app.cash.paparazzi") version "0.9.0" apply false
	id("net.twisterrob.root") version "0.14-20220205.192501-3"
	id("net.twisterrob.quality") version "0.14-20220205.192501-3"
	id("project-dependencies") apply false
	id("io.gitlab.arturbosch.detekt") version "1.19.0" apply false
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
