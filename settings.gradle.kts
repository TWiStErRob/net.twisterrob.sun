rootProject.name = "Sun"

include(":app")
include(":feature:configuration")
//include(":feature:preview")
include(":component:widget")
include(":component:states")
include(":component:lib")
include(":component:awt-hack")
include(":component:paparazzi")
include(":component:theme")

includeBuild("gradle/dependencies")
includeBuild("gradle/plugins")

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
			}
		}
		//maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
		//	name = "Sonatype 01: SNAPSHOTs"
		//	content {
		//		includeGroup("net.twisterrob.gradle")
		//	}
		//	mavenContent {
		//		snapshotsOnly()
		//	}
		//}
	}
	resolutionStrategy {
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
				// TODEL workaround https://github.com/cashapp/paparazzi/issues/343
				"app.cash.paparazzi" -> {
					useModule("app.cash.paparazzi:paparazzi-gradle-plugin:${requested.version}")
				}
			}
		}
	}
}

plugins {
	id("com.gradle.enterprise") version "3.7.1"
}

gradleEnterprise {
	buildScan {
		termsOfServiceUrl = "https://gradle.com/terms-of-service"
		termsOfServiceAgree = "yes"
	}
}
