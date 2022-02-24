plugins {
	id("project-module-java-library")
}

dependencies {
	api(Deps.AndroidX.annotations)
	api(Deps.Dagger.dagger)
	api(Deps.Kotlin.stdlib)
	api(Deps.Kotlin.stdlib8)
	api(Deps.Kotlin.bom)
}
