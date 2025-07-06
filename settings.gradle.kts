import net.twisterrob.gradle.doNotNagAbout
import net.twisterrob.gradle.settings.enableFeaturePreviewQuietly
import net.twisterrob.sun.plugins.isCI
import java.util.UUID

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
				includeGroup("org.gradle.android.cache-fix")
				includeGroup("gradle.plugin.org.gradle.android")
				includeGroup("org.jetbrains.kotlin.android")
				includeGroup("io.gitlab.arturbosch.detekt")
			}
		}
	}
}

plugins {
	id("com.gradle.develocity") version "4.0.2"
	id("net.twisterrob.gradle.plugin.nagging") version "0.18"
	id("project-settings")
}

develocity {
	buildScan {
		termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
		termsOfUseAgree = "yes"
		if (isCI) {
			fun setOutput(name: String, value: Any?) {
				// Using `appendText` to make sure out outputs are not cleared.
				// Using `\n` at the end to make sure further outputs are correct.
				val delimiter = UUID.randomUUID().toString()
				File(System.getenv("GITHUB_OUTPUT"))
					.appendText("${name}<<${delimiter}\n${value}\n${delimiter}\n")
			}

			buildScanPublished {
				setOutput("build-scan-url", buildScanUri.toASCIIString())
			}
			gradle.addBuildListener(object : BuildAdapter() {
				@Deprecated("Won't work with configuration caching.")
				override fun buildFinished(result: BuildResult) {
					setOutput("result-success", result.failure == null)
					setOutput("result-text", resultText(result))
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

val gradleVersion: String = GradleVersion.current().version

@Suppress("MaxLineLength")
doNotNagAbout(
	Regex(
		"""The (.+?)RuntimeClasspathCopy configuration has been deprecated for consumption\. """ +
				Regex.escape(
					"This will fail with an error in Gradle 9.0. " +
							"For more information, please refer to " +
							"https://docs.gradle.org/${gradleVersion}/userguide/declaring_dependencies.html#sec:deprecated-configurations" +
							" in the Gradle documentation."
				) +
				".*"
	),
)
@Suppress("MaxLineLength")
doNotNagAbout(
	Regex(
		"""While resolving configuration '(.+?)RuntimeClasspathCopy', it was also selected as a variant\. """ +
				Regex.escape(
					"Configurations should not act as both a resolution root and a variant simultaneously. " +
							"Depending on the resolved configuration in this manner has been deprecated. " +
							"This will fail with an error in Gradle 9.0. " +
							"Be sure to mark configurations meant for resolution as canBeConsumed=false or use the 'resolvable(String)' configuration factory method to create them. " +
							"Consult the upgrading guide for further information: " +
							"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#depending_on_root_configuration"
				) +
				".*"
	),
)

// TODEL https://issuetracker.google.com/issues/370546370
// See also https://github.com/gradle/gradle/issues/32422
@Suppress("MaxLineLength")
doNotNagAbout(
	"Declaring 'crunchPngs' as a property using an 'is-' method with a Boolean type on com.android.build.gradle.internal.dsl.BuildType\$AgpDecorated has been deprecated. " +
			"Starting with Gradle 10.0, this property will no longer be treated like a property. " +
			"The combination of method name and return type is not consistent with Java Bean property rules. " +
			"Add a method named 'getCrunchPngs' with the same behavior and mark the old one with @Deprecated, " +
			"or change the type of 'com.android.build.gradle.internal.dsl.BuildType\$AgpDecorated.isCrunchPngs' (and the setter) to 'boolean'. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#groovy_boolean_properties",
)
@Suppress("MaxLineLength")
doNotNagAbout(
	"Declaring 'useProguard' as a property using an 'is-' method with a Boolean type on com.android.build.gradle.internal.dsl.BuildType has been deprecated. " +
			"Starting with Gradle 10.0, this property will no longer be treated like a property. " +
			"The combination of method name and return type is not consistent with Java Bean property rules. " +
			"Add a method named 'getUseProguard' with the same behavior and mark the old one with @Deprecated, " +
			"or change the type of 'com.android.build.gradle.internal.dsl.BuildType.isUseProguard' (and the setter) to 'boolean'. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#groovy_boolean_properties",
)
@Suppress("MaxLineLength")
doNotNagAbout(
	"Declaring 'wearAppUnbundled' as a property using an 'is-' method with a Boolean type on com.android.build.api.variant.impl.ApplicationVariantImpl has been deprecated. " +
			"Starting with Gradle 10.0, this property will no longer be treated like a property. " +
			"The combination of method name and return type is not consistent with Java Bean property rules. " +
			"Add a method named 'getWearAppUnbundled' with the same behavior and mark the old one with @Deprecated, " +
			"or change the type of 'com.android.build.api.variant.impl.ApplicationVariantImpl.isWearAppUnbundled' (and the setter) to 'boolean'. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#groovy_boolean_properties",
)

// TODEL Gradle 8.14 vs AGP 8.9 https://issuetracker.google.com/issues/408334529
@Suppress("detekt.MaxLineLength")
doNotNagAbout(
	"Retrieving attribute with a null key. " +
			"This behavior has been deprecated. " +
			"This will fail with an error in Gradle 10. " +
			"Don't request attributes from attribute containers using null keys. " +
			"Consult the upgrading guide for further information: " +
			"https://docs.gradle.org/${gradleVersion}/userguide/upgrading_version_8.html#null-attribute-lookup",
	"at com.android.build.gradle.internal.ide.dependencies.ArtifactUtils.isAndroidProjectDependency(ArtifactUtils.kt:539)",
)
