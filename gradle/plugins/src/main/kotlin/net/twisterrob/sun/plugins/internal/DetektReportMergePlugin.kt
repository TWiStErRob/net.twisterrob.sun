package net.twisterrob.sun.plugins.internal

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
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
		output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))
	}
	tasks.withType<Detekt>().configureEach {
		reports {
			// https://sarifweb.azurewebsites.net
			sarif.required.set(true) // Github Code Scanning
		}
	}
	rootProject.tasks.named<ReportMergeTask>("detektReportMergeSarif") {
		val detektReportMergeTask = this@named
		tasks.withType<Detekt> {
			val detektReportingTask = this@withType
			detektReportMergeTask.mustRunAfter(detektReportingTask)
			detektReportMergeTask.input.from(detektReportingTask.sarifReportFile)
		}
	}
}

private fun Project.configureXML() {
	tasks.withType<Detekt>().configureEach {
		reports {
			xml.required.set(true)
		}
	}
	rootProject.tasks.maybeRegister<ReportMergeTask>("detektReportMergeXml") {
		output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.xml"))
	}
	rootProject.tasks.named<ReportMergeTask>("detektReportMergeXml") {
		val detektReportMergeTask = this@named
		tasks.withType<Detekt> {
			val detektReportingTask = this@withType
			detektReportMergeTask.mustRunAfter(detektReportingTask)
			detektReportMergeTask.input.from(detektReportingTask.xmlReportFile)
		}
	}
}
