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
		output.set(rootProject.buildDir.resolve("reports/detekt/merge.sarif"))
	}
	tasks.withType<Detekt>().configureEach {
		reports {
			// https://sarifweb.azurewebsites.net
			sarif.required.set(true) // Github Code Scanning
		}
	}
	val detektReportMergeSarif =
		rootProject.tasks.named<ReportMergeTask>("detektReportMergeSarif")
	tasks.withType<Detekt> {
		detektReportMergeSarif.configure {
			mustRunAfter(this@withType)
			input.from(this@withType.sarifReportFile)
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
		output.set(rootProject.buildDir.resolve("reports/detekt/merge.xml"))
	}
	val detektReportMergeXml =
		rootProject.tasks.named<ReportMergeTask>("detektReportMergeXml")
	tasks.withType<Detekt> {
		detektReportMergeXml.configure {
			mustRunAfter(this@withType)
			input.from(this@withType.xmlReportFile)
		}
	}
}
