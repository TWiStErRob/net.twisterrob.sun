package net.twisterrob.sun.plugins.internal

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

fun Project.detekt(block: DetektExtension.() -> Unit) {
	project.extensions.configure(block)
}
