rootProject.name = "Sun"

include(":app")
include(":lib")

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
				"com.android.application" -> {
					useModule("com.android.tools.build:gradle:4.2.2")
				}
				"net.twisterrob.root",
				"net.twisterrob.java",
				"net.twisterrob.android-lib",
				"net.twisterrob.android-app" -> {
	   				useModule("net.twisterrob.gradle:twister-convention-plugins:0.12")
				}
			}
		}
	}
}
