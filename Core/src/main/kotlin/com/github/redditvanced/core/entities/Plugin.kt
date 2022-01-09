package com.github.redditvanced.core.entities

import android.content.res.Resources
import androidx.annotation.CallSuper
import com.github.redditvanced.core.managers.PluginManager
import com.github.redditvanced.core.patcher.Patcher
import com.github.redditvanced.core.util.Logger

@Suppress("MemberVisibilityCanBePrivate")
open class Plugin(
	/**
	 * The manifest that was loaded from the generated manifest.json of the plugin package
	 */
	val manifest: PluginManifest,
) {
	/**
	 * The plugins [Logger]. Only use this for logging information.
	 */
	val logger: Logger = Logger(manifest.name)

	/**
	 * The [Patcher] for this class.
	 * Add/Remove patches here.
	 */
	val patcher: Patcher = Patcher(logger)

	/**
	 * The Android resources of your plugin.
	 * You need to set [RedditVancedPlugin.loadResources] in order to load resources
	 */
	lateinit var resources: Resources

	/**
	 * Whether the plugin is currently running
	 */
	var isStarted: Boolean = false

	/**
	 * Checks if this plugin is enabled.
	 * Alias for [PluginManager.isPluginEnabled]
	 */
	fun isEnabled() = PluginManager.isPluginEnabled(manifest.name)

	/**
	 * Whether the user will be prompted to restart after enabling/disabling.
	 * @return [PluginManifest.requiresRestart] or override this to conditionally prompt
	 */
	open fun requiresRestart() =
		manifest.requiresRestart

	// ------------------- Plugin Lifecycle Methods -------------------

	/**
	 * Called when your plugin is loaded (before starting)
	 */
	open fun onLoad() {}

	/**
	 * Called when your plugin is started
	 */
	open fun onStart() {}

	/**
	 * Called when your plugin is stopped
	 */
	@CallSuper
	open fun onStop() {
		patcher.unpatchAll()
	}

	/**
	 * Called when your plugin is unloaded
	 */
	open fun onUnload() {}

	/**
	 * Called when your plugin is about to be updated
	 * @param nextVersion The version of this plugin that is going to be installed
	 */
	open fun onUpdate(nextVersion: String) {}

	/**
	 * Called after your plugin has been updated, and reloaded (This is called on the new instance of the plugin)
	 * @param previousVersion The version of the plugin that was previously installed
	 */
	open fun onAfterUpdate(previousVersion: String) {}
}
