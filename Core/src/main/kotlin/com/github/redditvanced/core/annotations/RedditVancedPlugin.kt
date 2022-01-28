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
	 * Whether to load the plugin's resources (if any)
	 */
	val loadResources: Boolean = false,

	/**
	 * Prompts the user to restart Aliucord after:
	 * - Enabling/Disabling manually
	 * - Updating
	 * - Uninstalling
	 * - Reinstalling
	 */
	val requiresRestart: Boolean = false,
)
