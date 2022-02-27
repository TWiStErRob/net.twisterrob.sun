dependencyResolutionManagement {
	@Suppress("UnstableApiUsage") // TODEL Gradle 7.x
	versionCatalogs {
		create("libs") {
			from(files("../libs.versions.toml"))
		}
	}
}
