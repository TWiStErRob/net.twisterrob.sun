package net.twisterrob.sun.plugins.internal

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

internal class DetektReportMergePlugin : Plugin<Project> {

	override fun apply(project: Project) {
		configureSarif(project)
		configureXML(project)
	}

	private fun configureSarif(project: Project) {
		project.rootProject.tasks.maybeRegister<ReportMergeTask>("detektReportMergeSarif") {
			output.set(project.rootProject.buildDir.resolve("reports/detekt/merge.sarif"))
		}
		project.tasks.withType<Detekt>().configureEach {
			reports {
				xml.required.set(true) // Github Code Scanning
				// https://sarifweb.azurewebsites.net
				sarif.required.set(true) // Github Code Scanning
			}
		}
		val detektReportMergeSarif =
			project.rootProject.tasks.named<ReportMergeTask>("detektReportMergeSarif")
		project.tasks.withType<Detekt> {
			detektReportMergeSarif.configure {
				mustRunAfter(this@withType)
				input.from(this@withType.sarifReportFile)
			}
		}
	}

	private fun configureXML(project: Project) {
		project.tasks.withType<Detekt>().configureEach {
			reports {
				xml.required.set(true) // Github Code Scanning
			}
		}
		project.rootProject.tasks.maybeRegister<ReportMergeTask>("detektReportMergeXml") {
			output.set(project.rootProject.buildDir.resolve("reports/detekt/merge.xml"))
		}
		val detektReportMergeXml =
			project.rootProject.tasks.named<ReportMergeTask>("detektReportMergeXml")
		project.tasks.withType<Detekt> {
			detektReportMergeXml.configure {
				mustRunAfter(this@withType)
				input.from(this@withType.xmlReportFile)
			}
		}
	}
}
