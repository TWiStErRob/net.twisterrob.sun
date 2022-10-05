rootProject.name = "plugins"

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
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
	versionCatalogs {
		create("libs") {
			from(files("../libs.versions.toml"))
		}
	}
}
