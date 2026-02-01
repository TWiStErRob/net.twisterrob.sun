package net.twisterrob.sun.plugins.internal

import dev.detekt.gradle.Detekt
import dev.detekt.gradle.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

internal class DetektReportMergePlugin : Plugin<Project> {

	override fun apply(project: Project) {
		project.configureSarif()
		project.configureXML()
	}
}

private fun Project.configureSarif() {
	rootProject.tasks.maybeRegister<ReportMergeTask>("detektReportMergeSarif") {
		output = rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif")
	}
	tasks.withType<Detekt>().configureEach {
		reports {
			// https://sarifweb.azurewebsites.net
			sarif.required = true // Github Code Scanning
		}
	}
	rootProject.tasks.named<ReportMergeTask>("detektReportMergeSarif") {
		val detektReportMergeTask = this@named
		tasks.withType<Detekt> {
			val detektReportingTask = this@withType
			detektReportMergeTask.mustRunAfter(detektReportingTask)
			detektReportMergeTask.input.from(detektReportingTask.reports.sarif.outputLocation)
		}
	}
}

private fun Project.configureXML() {
	tasks.withType<Detekt>().configureEach {
		reports {
			checkstyle.required = true
		}
	}
	rootProject.tasks.maybeRegister<ReportMergeTask>("detektReportMergeXml") {
		output = rootProject.layout.buildDirectory.file("reports/detekt/merge.xml")
	}
	rootProject.tasks.named<ReportMergeTask>("detektReportMergeXml") {
		val detektReportMergeTask = this@named
		tasks.withType<Detekt> {
			val detektReportingTask = this@withType
			detektReportMergeTask.mustRunAfter(detektReportingTask)
			detektReportMergeTask.input.from(detektReportingTask.reports.checkstyle.outputLocation)
		}
	}
}
