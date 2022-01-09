package com.github.redditvanced.core.annotations

/**
 * Annotates the entrypoint of a plugin, used by manifest.json generation.
 * Only one class can be annotated by this per plugin.
 */
@Suppress("unused")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class RedditVancedPlugin(
	/**
	 * Whether your plugin has resources that need to be loaded
	 */
	val loadResources: Boolean,

	/**
	 * Prompts the user to restart Aliucord after:
	 * - Enabling manually
	 * - Disabling manually
	 * - Updating
	 * - Uninstalling
	 */
	val requiresRestart: Boolean,
)
