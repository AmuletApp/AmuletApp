package com.github.redditvanced.core.managers

import android.content.res.AssetManager
import android.content.res.Resources
import com.github.redditvanced.core.Constants.Paths
import com.github.redditvanced.core.coreplugins.CorePlugins
import com.github.redditvanced.core.entities.Plugin
import com.github.redditvanced.core.models.PluginManifest
import com.github.redditvanced.core.util.Logger
import com.github.redditvanced.core.util.Utils
import dalvik.system.PathClassLoader
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
object PluginManager {
	private val logger = Logger("PluginManager")

	/**
	 * All currently loaded plugins.
	 * Mapped by plugin name.
	 */
	val plugins = sortedMapOf<String, Plugin>()

	/**
	 * All plugin class loaders.
	 * Mapped by a plugin's classloader to the plugin.
	 * This is primarily used for crashlogs.
	 */
	val classLoaders = hashMapOf<PathClassLoader, Plugin>()

	/**
	 * Load all plugins from the plugins folder
	 * Only to be used internally
	 */
	fun loadAllPlugins() {
		plugins.putAll(CorePlugins.plugins.map { it.manifest.name to it })

		val files = try {
			File(Paths.PLUGINS).listFiles()
				?.filter { f -> f.endsWith(Paths.PLUGIN_EXT) }
				?.sorted()
				?: return
		} catch (t: Throwable) {
			logger.error("Failed to read plugin directory from disk", t)
			return
		}

		val loadErrors = files.map { f -> loadPlugin(f.nameWithoutExtension) }.count { it }
		if (loadErrors > 0) {
			logger.errorToast("Loaded plugins with $loadErrors ${Utils.pluralise(loadErrors, "error")}, check log for more details")
		}
	}

	/**
	 * Load a plugin by its name (without extension)
	 * This unsafely overrides any current plugin with the same name
	 * @return Plugin load successful
	 */
	private fun loadPlugin(name: String): Boolean {
		logger.info("Loading plugin $name")
		val path = "${Paths.PLUGINS}/$name/${Paths.PLUGIN_EXT}"
		val ctx = Utils.appContext

		try {
			val loader = PathClassLoader(path, ctx.classLoader)

			val manifestStream = loader.getResourceAsStream("manifest.json")
			if (manifestStream == null) {
				logger.error("Missing manifest for plugin $name")
				return false
			}

			val manifest = try {
				Json.decodeFromStream<PluginManifest>(manifestStream)
			} catch (t: SerializationException) {
				logger.error("Invalid manifest for plugin $name", t)
				return false
			}

			val pluginClass = loader.loadClass(manifest.pluginClass)
			val plugin = pluginClass.getConstructor(PluginManifest::class.java).newInstance(manifest) as Plugin

			if (manifest.loadResources) {
				// based on https://stackoverflow.com/questions/7483568/dynamic-resource-loading-from-other-apk
				val assets = AssetManager::class.java.newInstance()
				val addAssetPath = AssetManager::class.java.getMethod("addAssetPath", String::class.java)
				addAssetPath.invoke(assets, path)
				plugin.resources = Resources(assets, ctx.resources.displayMetrics, ctx.resources.configuration)
			}

			plugins[manifest.name] = plugin
			classLoaders[loader] = plugin
			plugin.onLoad()
		} catch (t: Throwable) {
			logger.error("Failed to load plugin $name", t)
			return false
		}
		return true
	}

	/**
	 * Start all currently loaded and enabled plugins
	 */
	fun startPlugins() {
		plugins.keys
			.filter(PluginManager::isPluginEnabled)
			.forEach(PluginManager::startPlugin)
	}

	/**
	 * Starts a single plugin
	 * @throws IllegalStateException If plugin not loaded
	 */
	fun startPlugin(name: String) {
		val plugin = plugins[name]
			?: throw IllegalStateException("Plugin $name is not loaded")

		try {
			plugin.isStarted = true
			plugin.onStart()
		} catch (t: Throwable) {
			plugin.logger.error(t)
		}
	}

	/**
	 * Checks whether a plugin is enabled
	 * @param name Plugin name
	 */
	fun isPluginEnabled(name: String): Boolean {
		// TODO: implement settings
		return true
	}
}
