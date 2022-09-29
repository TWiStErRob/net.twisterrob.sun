package net.twisterrob.sun.plugins.internal

import net.twisterrob.sun.plugins.isCI
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

internal class GitHubActionsPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		project.tasks.withType<Test>().configureEach {
			ignoreFailures = isCI
		}
		project.plugins.withId("io.gitlab.arturbosch.detekt") {
			project.detekt {
				ignoreFailures = isCI
			}
		}
	}
}
