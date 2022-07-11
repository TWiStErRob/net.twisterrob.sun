package net.twisterrob.sun.plugins.internal

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import net.twisterrob.sun.plugins.isCI
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
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
		val project = this@commonJavaConfig
		val detekt = project.extensions.getByName<DetektExtension>("detekt")
		detekt.apply {
			toolVersion = "main-SNAPSHOT"
			project.configurations.configureEach {
				if (name == "detekt") {
					resolutionStrategy {
						failOnNonReproducibleResolution()
						eachDependency {
							if (requested.group == "io.gitlab.arturbosch.detekt" && requested.version == "main-SNAPSHOT") {
								useVersion(
									when (requested.name) {
										"detekt-cli" -> "main-20220711.191117-734"
										"detekt-parser" -> "main-20220711.191117-733"
										"detekt-tooling" -> "main-20220711.191117-731"
										"detekt-api" -> "main-20220711.191117-737"
										"detekt-psi-utils" -> "main-20220711.191117-733"
										"detekt-core" -> "main-20220711.191117-737"
										"detekt-utils" -> "main-20220711.191117-391"
										"detekt-metrics" -> "main-20220711.191117-734"
										"detekt-report-html",
										"detekt-report-txt",
										"detekt-report-xml",
										"detekt-report-sarif" -> "main-20220711.191117-731"
										"detekt-report-md" -> "main-20220711.191117-60"
										"detekt-rules",
										"detekt-rules-complexity",
										"detekt-rules-coroutines",
										"detekt-rules-documentation",
										"detekt-rules-empty",
										"detekt-rules-errorprone",
										"detekt-rules-exceptions",
										"detekt-rules-naming",
										"detekt-rules-performance",
										"detekt-rules-style" -> "main-20220711.191117-731"
										else -> error("Unpinned module: ${requested}")
									}
								)
							}
						}
					}
				}
			}
			ignoreFailures = true
			// TODEL https://github.com/detekt/detekt/issues/4926
			buildUponDefaultConfig = false
			allRules = true
			//debug = true
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
			detektReportMergeSarif.configure {
				mustRunAfter(this@withType)
				input.from(this@withType.sarifReportFile)
			}
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
	tasks.withType<Test>().configureEach {
		ignoreFailures = isCI
	}
}
