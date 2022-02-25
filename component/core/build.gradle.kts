plugins {
	id("project-module-java-library")
}

dependencies {
	api(libs.androidx.annotations)
	api(libs.dagger)
	api(libs.kotlin.stdlib)
	api(libs.kotlin.stdlib8)
	api(libs.kotlin.bom)
}
