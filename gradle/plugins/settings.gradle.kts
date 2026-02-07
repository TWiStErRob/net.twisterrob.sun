rootProject.name = "plugins"

dependencyResolutionManagement {
	repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
		maven("https://central.sonatype.com/repository/maven-snapshots/") {
			name = "Central SNAPSHOTs"
			content {
				includeGroup("net.twisterrob.gradle")
				includeGroupByRegex("""net\.twisterrob\.gradle\.plugin\..*""")
			}
			mavenContent {
				snapshotsOnly()
			}
		}
	}
	versionCatalogs {
		create("libs") {
			from(files("../libs.versions.toml"))
		}
	}
}
