package com.github.redditvanced.core.entities

import kotlinx.serialization.Serializable

@Serializable
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
	@Serializable
	data class Author(
		val name: String,
		val discordId: Long?,
		val redditUsername: String?,
	)
}
