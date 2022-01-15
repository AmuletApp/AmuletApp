package com.github.redditvanced.core.coreplugins

import com.github.redditvanced.core.entities.Plugin
import com.github.redditvanced.common.models.PluginManifest

object CorePlugins {
	/**
	 * All unsorted core plugins.
	 */
	val plugins = listOf(
		NoAds()
	)
}

/**
 * Alias to separate core plugins from regular plugins
 */
open class CorePlugin(name: String, description: String) : Plugin(PluginManifest(
	name,
	description,
	listOf(PluginManifest.Author("Aliucord", null, null)),
	loadResources = false,
	requiresRestart = false,
	pluginClass = "",
	changelog = "",
	version = ""
))
