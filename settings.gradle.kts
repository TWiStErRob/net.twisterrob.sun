rootProject.name = "Sun"

include(":app")
include(":app:widget")
include(":app:states")
include(":app:configuration")
include(":app:preview")
include(":lib")

includeBuild("gradle/dependencies")
includeBuild("gradle/plugins")

pluginManagement {
	repositories {
		google()
		mavenCentral()
		//maven { name = "Sonatype SNAPSHOTs s01"; setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
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
			}
		}
	}
}
