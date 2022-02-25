// TODEL Gradle 7.4
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
	@Suppress("UnstableApiUsage") // TODEL Gradle 7.4
	versionCatalogs {
		create("libs") {
			from(files("../libs.versions.toml"))
		}
	}
}
