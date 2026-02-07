rootProject.name = "plugins"

dependencyResolutionManagement {
	repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
		exclusiveContent {
			forRepository {
				mavenLocal()
			}
			filter {
				includeVersionByRegex(
					"""^net\.twisterrob\.gradle$""",
					".*",
					"^${Regex.escape("0.19-SNAPSHOT")}$"
				)
				includeVersionByRegex(
					"""^net\.twisterrob\.gradle\.plugin\.[^.]+$""",
					".*",
					"^${Regex.escape("0.19-SNAPSHOT")}$"
				)
			}
		}
	}
	versionCatalogs {
		create("libs") {
			from(files("../libs.versions.toml"))
		}
	}
}
