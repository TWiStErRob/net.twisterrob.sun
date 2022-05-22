package net.twisterrob.sun.plugins.internal

import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

@Suppress("RemoveExplicitTypeArguments")
inline fun <reified T : Task> TaskContainer.maybeRegister(
	taskName: String,
	noinline configuration: T.() -> Unit
): TaskProvider<T> =
	try {
		named<T>(taskName)
	} catch (ex: UnknownTaskException) {
		register<T>(taskName, configuration)
	}
