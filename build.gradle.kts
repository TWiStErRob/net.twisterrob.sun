plugins {
	id("project-module-root")
	id("net.twisterrob.gradle.plugin.root")
	id("net.twisterrob.gradle.plugin.quality")
}

tasks.register("check") {
	description = "Delegate task for checking included builds too."
	dependsOn(gradle.includedBuild("plugins").task(":check"))
}
