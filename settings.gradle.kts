import net.twisterrob.gradle.doNotNagAbout
import net.twisterrob.gradle.settings.enableFeaturePreviewQuietly
import net.twisterrob.sun.plugins.isCI

rootProject.name = "Sun"

enableFeaturePreviewQuietly("TYPESAFE_PROJECT_ACCESSORS", "Type-safe project accessors")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

include(":app")
include(":feature:configuration")
include(":feature:preview")
include(":component:core")
include(":component:widget")
include(":component:states")
include(":component:lib")
include(":component:paparazzi")
include(":component:theme")

dependencyResolutionManagement {
	repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
	repositories {
		google()
		mavenCentral()
	}
}

pluginManagement {
	includeBuild("gradle/plugins")
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
				includeGroup("com.gradle.develocity")
				includeGroup("org.gradle.toolchains")
				includeGroup("org.gradle.toolchains.foojay-resolver-convention")
				includeGroup("org.gradle.android.cache-fix")
				includeGroup("gradle.plugin.org.gradle.android")
				includeGroup("org.jetbrains.kotlin.android")
				includeGroup("io.gitlab.arturbosch.detekt")
			}
		}
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
	id("com.gradle.develocity") version "4.3"
	id("net.twisterrob.gradle.plugin.nagging") version "0.18"
	id("project-settings")
}

develocity {
	buildScan {
		termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
		termsOfUseAgree = "yes"
		if (isCI) {
			val githubOutput = providers.environmentVariable("GITHUB_OUTPUT").map(::File) // CC
			fun setOutput(name: String, value: Any?) {
				// Using `appendText` to make sure out outputs are not cleared.
				// Using `\n` to make sure further outputs are correct.
				// Using `delimiter` to ensure that any special characters (such as newlines) are escaped.
				val delimiter = java.util.UUID.randomUUID().toString()
				githubOutput
					// TODEL https://github.com/gradle/gradle/issues/25716
					.orNull.let { it ?: error("GITHUB_OUTPUT environment variable is not set.") }
					.appendText("${name}<<${delimiter}\n${value}\n${delimiter}\n")
			}

			buildScanPublished {
				setOutput("build-scan-url", buildScanUri.toASCIIString())
			}
			val rootDir = rootDir // CC
			buildFinished {
				setOutput("result-success", failures.isEmpty())
				val resultText: String = when {
					failures.isEmpty() ->
						"Build Successful"
					failures.size == 1 && failures.single() is org.gradle.internal.exceptions.LocationAwareException ->
						// Shorten the trivial part of the file name,
						// as there's a length limitation in GitHub Actions for message (140 chars).
						// > Build Failed: Build file '.../build.gradle.kts' line: 20
						// > A problem occurred configuring project ':feature:about'.
						"Build Failed: ${failures.single().message?.replace(rootDir.absolutePath, "...")?.replace("\n", " ")}"
					else ->
						"Build Failed with ${failures.size} failures"
				}
				setOutput("result-text", resultText)
			}
		}
	}
}

val gradleVersion: String = GradleVersion.current().version

// TODEL Gradle 9.1 vs detekt 1.23.8 https://github.com/detekt/detekt/issues/8452
@Suppress("detekt.MaxLineLength")
doNotNagAbout(
	"The ReportingExtension.file(String) method has been deprecated. " +
			"This is scheduled to be removed in Gradle 10. " +
			"Please use the getBaseDirectory().file(String) or getBaseDirectory().dir(String) method instead. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_9.html#reporting_extension_file",
	"at io.gitlab.arturbosch.detekt.DetektPlugin.apply(DetektPlugin.kt:28)",
)

// TODEL Gradle 9.1 vs AGP 8.13 https://issuetracker.google.com/issues/444260628
@Suppress("detekt.MaxLineLength")
doNotNagAbout(
	Regex(
		"Declaring dependencies using multi-string notation has been deprecated. ".escape() +
				"This will fail with an error in Gradle 10. ".escape() +
				"Please use single-string notation instead: ".escape() +
				"\"${"com.android.tools.build:aapt2:".escape()}\\d+\\.\\d+\\.\\d+-\\d+:(windows|linux|osx)${"\". ".escape()}" +
				"Consult the upgrading guide for further information: ".escape() +
				"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_9.html#dependency_multi_string_notation".escape() +
				".*",
	),
	//"at com.android.build.gradle.internal.res.Aapt2FromMaven\$Companion.create(Aapt2FromMaven.kt:139)",
)

// TODEL Gradle 9.1 vs AGP 8.13 https://issuetracker.google.com/issues/444260628
@Suppress("detekt.MaxLineLength")
doNotNagAbout(
	Regex(
		"Declaring dependencies using multi-string notation has been deprecated. ".escape() +
				"This will fail with an error in Gradle 10. ".escape() +
				"Please use single-string notation instead: ".escape() +
				"\"${"com.android.tools.lint:lint-gradle:".escape()}\\d+\\.\\d+\\.\\d+${"\". ".escape()}" +
				"Consult the upgrading guide for further information: ".escape() +
				"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_9.html#dependency_multi_string_notation".escape() +
				".*",
	),
	//"at com.android.build.gradle.internal.lint.LintFromMaven\$Companion.from(AndroidLintInputs.kt:2850)",
)

private fun String.escape(): String = Regex.escape(this)
