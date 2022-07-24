package net.twisterrob.sun.plugins.internal

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

class DetektPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		project.plugins.apply("io.gitlab.arturbosch.detekt")
		val rootProject = project.rootProject
		project.detekt {
			ignoreFailures = true
			// TODEL https://github.com/detekt/detekt/issues/4926
			buildUponDefaultConfig = false
			allRules = true
			//debug = true
			config = rootProject.files("config/detekt/detekt.yml")
			baseline = rootProject.file("config/detekt/detekt-baseline-${project.name}.xml")
			basePath = rootProject.projectDir.absolutePath

			parallel = true

			project.tasks.withType<Detekt>().configureEach {
				reports {
					html.required.set(true) // human
					xml.required.set(true) // checkstyle
					txt.required.set(true) // console
					// https://sarifweb.azurewebsites.net
					sarif.required.set(true) // Github Code Scanning
				}
			}
		}

		val detektReportMergeSarif =
			rootProject.tasks.named<ReportMergeTask>("detektReportMergeSarif")
		project.tasks.withType<Detekt> {
			detektReportMergeSarif.configure {
				mustRunAfter(this@withType)
				input.from(this@withType.sarifReportFile)
			}
		}

		val detektReportMergeXml =
			rootProject.tasks.named<ReportMergeTask>("detektReportMergeXml")
		project.tasks.withType<Detekt> {
			detektReportMergeXml.configure {
				mustRunAfter(this@withType)
				input.from(this@withType.xmlReportFile)
			}
		}
	}
}
