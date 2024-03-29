package net.twisterrob.sun.plugins.internal

import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

internal inline fun <reified T : Task> TaskContainer.maybeRegister(
	taskName: String,
	noinline configuration: T.() -> Unit
): TaskProvider<T> =
	try {
		this.named<T>(taskName)
	} catch (ex: UnknownTaskException) {
		@Suppress("RemoveExplicitTypeArguments")
		this.register<T>(taskName, configuration)
	}
