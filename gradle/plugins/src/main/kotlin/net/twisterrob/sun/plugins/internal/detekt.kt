package net.twisterrob.sun.plugins.internal

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.detekt(block: DetektExtension.() -> Unit) {
	this.extensions.configure(block)
}
