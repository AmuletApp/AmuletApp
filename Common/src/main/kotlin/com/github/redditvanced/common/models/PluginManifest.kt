package com.github.redditvanced.common.models

/**
 * Serializable json class for the manifest.json located in each plugin
 */
data class PluginManifest(
	val name: String,
	val description: String,
	val authors: List<Author>,
	val loadResources: Boolean,
	val requiresRestart: Boolean,
	val pluginClass: String,
	val changelog: String,
	val version: String,
) {
	data class Author(
		val name: String,
		val discordId: Long?,
		val redditUsername: String?,
	)
}
