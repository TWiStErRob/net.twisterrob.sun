package net.twisterrob.sun.plugins.internal

import org.gradle.api.plugins.PluginContainer
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency

internal fun PluginContainer.apply(plugin: Provider<PluginDependency>) {
	apply(plugin.get().pluginId)
}
