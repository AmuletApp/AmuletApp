package com.github.redditvanced.core.coreplugins

import com.github.redditvanced.common.models.PluginManifest
import com.github.redditvanced.common.models.PluginManifest.Author
import com.github.redditvanced.core.entities.Plugin

object CorePlugins {
	/**
	 * All unsorted core plugins.
	 */
	val plugins = listOf(
		NoAds(),
		DebugPage(),
	)
}

/**
 * Alias to separate core plugins from regular plugins
 */
open class CorePlugin(name: String, description: String) : Plugin(
	PluginManifest(
		name,
		description,
		listOf(Author("Amulet", null, null)),
		loadResources = false,
		requiresRestart = false,
		pluginClass = "",
		changelog = "",
		version = ""
	)
)
