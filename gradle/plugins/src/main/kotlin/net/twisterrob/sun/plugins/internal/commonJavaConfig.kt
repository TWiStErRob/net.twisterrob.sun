package net.twisterrob.sun.plugins.internal

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.commonJavaConfig() {
	val javaVersion = JavaVersion.toVersion(libs.versions.java.get())
	tasks.withType<JavaCompile> {
		sourceCompatibility = javaVersion.toString()
		targetCompatibility = javaVersion.toString()
		options.compilerArgs = options.compilerArgs + listOf(
			// Enable all warnings during compilation.
			"-Xlint:all",
			// Workaround for https://github.com/cashapp/paparazzi/issues/362.
			"-Xlint:-classfile",
			// > No processor claimed any of these annotations:
			// > javax.inject.Inject,
			// > androidx.annotation.Nullable,
			// > androidx.annotation.RequiresPermission,
			// > androidx.annotation.NonNull,
			// > dagger.Component,android.annotation.TargetApi
			"-Xlint:-processing",
			// Fail build when warnings pop up.
			"-Werror"
		)
	}
	tasks.withType<KotlinCompile> {
		kotlinOptions {
			jvmTarget = javaVersion.toString()
			allWarningsAsErrors = true
		}
	}
	plugins.apply("io.gitlab.arturbosch.detekt")
	plugins.withId("io.gitlab.arturbosch.detekt") {
		val detekt = this@commonJavaConfig.extensions.getByName<DetektExtension>("detekt")
		detekt.apply {
			ignoreFailures = true
			buildUponDefaultConfig = true
			allRules = true
			config = rootProject.files("config/detekt/detekt.yml")
			baseline = rootProject.file("config/detekt/detekt-baseline-${project.name}.xml")
			basePath = rootProject.projectDir.absolutePath

			parallel = true

			tasks.withType<Detekt>().configureEach {
				// Target version of the generated JVM bytecode. It is used for type resolution.
				jvmTarget = javaVersion.toString()
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
		tasks.withType<Detekt> {
			detektReportMergeSarif.configure { input.from(this@withType.sarifReportFile) }
		}

		val detektReportMergeXml =
			rootProject.tasks.named<ReportMergeTask>("detektReportMergeXml")
		tasks.withType<Detekt> {
			detektReportMergeXml.configure { input.from(this@withType.xmlReportFile) }
		}
	}
}