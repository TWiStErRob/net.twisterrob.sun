import groovy.json.JsonOutput.toJson

rootProject.name = "Sun"

enableFeaturePreviewQuietly("TYPESAFE_PROJECT_ACCESSORS", "Type-safe project accessors")

include(":app")
include(":feature:configuration")
include(":feature:preview")
include(":component:core")
include(":component:widget")
include(":component:states")
include(":component:lib")
include(":component:paparazzi")
include(":component:theme")

includeBuild("gradle/plugins")

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
	}
}

pluginManagement {
	repositories {
		google {
			content {
				includeGroupByRegex("""^com\.android(\..*)?$""")
				includeGroupByRegex("""^com\.google\..*$""")
				includeGroupByRegex("""^androidx\..*$""")
			}
		}
		mavenCentral()
		gradlePluginPortal {
			content {
				includeGroup("com.gradle")
				includeGroup("com.gradle.enterprise")
				includeGroup("gradle.plugin.org.gradle.android")
				includeGroup("org.jetbrains.kotlin.android")
				includeGroup("io.gitlab.arturbosch.detekt")
			}
		}
		maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
			name = "Sonatype 01: SNAPSHOTs"
			content {
				includeVersionByRegex("""^net\.twisterrob\.gradle$""", ".*", """.*-SNAPSHOT$""")
				includeVersionByRegex("""^net\.twisterrob\.gradle$""", ".*", """.*-\d{8}\.\d{6}-\d+$""")
			}
			mavenContent {
				// This doesn't allow using specific snapshot, so using versionRegex above.
				//snapshotsOnly()
			}
		}
	}
	resolutionStrategy {
		// Not possible here, see root build.gradle.
		//cacheChangingModulesFor(0, "seconds") // -SNAPSHOT

		eachPlugin {
			// REPORT requested.version is null when using plugins {} block just above on Gradle 6.9.1.
			when (requested.id.id) {
				"net.twisterrob.root",
				"net.twisterrob.vcs",
				"net.twisterrob.java",
				"net.twisterrob.java-library",
				"net.twisterrob.kotlin",
				"net.twisterrob.android-app",
				"net.twisterrob.android-lib",
				"net.twisterrob.android-test" -> {
					useModule("net.twisterrob.gradle:twister-convention-plugins:${requested.version}")
				}
				"net.twisterrob.quality" -> {
					useModule("net.twisterrob.gradle:twister-quality:${requested.version}")
				}
			}
		}
	}
}

plugins {
	id("com.gradle.enterprise") version "3.10.2"
}

gradleEnterprise {
	buildScan {
		termsOfServiceUrl = "https://gradle.com/terms-of-service"
		termsOfServiceAgree = "yes"
		// https://docs.github.com/en/actions/learn-github-actions/environment-variables#default-environment-variables
		if (System.getenv("GITHUB_ACTIONS") == "true") {
			buildScanPublished {
				println("::set-output name=build-scan-url::${toJson(this@buildScanPublished.buildScanUri.toString())}")
			}
			gradle.addBuildListener(object : BuildAdapter() {
				@Deprecated("Won't work with configuration caching.")
				override fun buildFinished(result: BuildResult) {
					println("::set-output name=result-success::${toJson(result.failure == null)}")
					println("::set-output name=result-text::${toJson(resultText(result))}")
				}

				private fun resultText(result: BuildResult): String =
				    "${result.action} ${resultText(result.failure)}"

				private fun resultText(ex: Throwable?): String =
					when (ex) {
						null ->
							"Successful"
						is org.gradle.internal.exceptions.LocationAwareException ->
							"Failed: ${ex.message}"
						else ->
							"Failed with ${ex}"
					}
			})
		}
	}
}

/**
 * @see <a href="https://github.com/gradle/gradle/issues/19069">Feature request</a>
 */
fun Settings.enableFeaturePreviewQuietly(name: String, summary: String) {
	enableFeaturePreview(name)
	val logger: Any = org.gradle.util.internal.IncubationLogger::class.java
		.getDeclaredField("INCUBATING_FEATURE_HANDLER")
		.apply { isAccessible = true }
		.get(null)

	@Suppress("UNCHECKED_CAST")
	val features: MutableSet<String> =
		org.gradle.internal.featurelifecycle.LoggingIncubatingFeatureHandler::class.java
			.getDeclaredField("features")
			.apply { isAccessible = true }
			.get(logger) as MutableSet<String>

	features.add(summary)
}
