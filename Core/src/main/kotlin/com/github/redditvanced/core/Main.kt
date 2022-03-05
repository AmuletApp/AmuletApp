package com.github.redditvanced.core

import android.os.Looper
import com.beust.klaxon.Klaxon
import com.github.redditvanced.common.Constants
import com.github.redditvanced.common.models.CoreSettings
import com.github.redditvanced.core.managers.PluginManager
import com.github.redditvanced.core.patcher.Patcher
import com.github.redditvanced.core.util.*
import com.reddit.frontpage.main.MainActivity
import java.io.*
import java.sql.Timestamp
import kotlin.concurrent.thread
import kotlin.system.exitProcess

@Suppress("unused")
object Main {
	private val logger = Logger("Core")
	private val patcher = Patcher(logger)
	lateinit var settings: CoreSettings
	private var initialized = false

	/**
	 * Init hook called by the Injector
	 */
	@JvmStatic
	@Suppress("unused")
	fun init(activity: MainActivity) {
		if (initialized) return
		initialized = true

		Utils.appActivity = activity
		Utils.init(patcher)

		// Handle crashes
		Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
			if (Looper.getMainLooper().thread !== thread) {
				logger.error("Uncaught exception on thread " + thread.name, throwable)
				return@setDefaultUncaughtExceptionHandler
			}

			thread {
				Looper.prepare()

				// Find the plugin that caused the crash (if any)
				var crashedPlugin: String? = null
				loop@ for (element in throwable.stackTrace) {
					for ((loader, plugin) in PluginManager.classLoaders.entries) {
						val loadedClass = try {
							loader.loadClass(element.className)
						} catch (_: ClassNotFoundException) {
							continue
						}

						if (loadedClass.classLoader != loader) {
							// class was loaded from the parent classloader, ignore
							continue
						}

						crashedPlugin = plugin.manifest.name
						break@loop
					}
				}

				// Write crashlog to disk
				val crashlogName = Timestamp(System.currentTimeMillis()).toString().replace(":".toRegex(), "_") + ".txt"
				val file = File(Constants.Paths.CRASHLOGS, crashlogName)
				PrintStream(file).use { ps -> throwable.printStackTrace(ps) }

				// Show error toast to user
				var msg = "An unrecoverable crash occurred"
				if (crashedPlugin != null) {
					// disable plugin if auto crash disable and plugin enabled
					msg = "An unrecoverable crash was caused by $crashedPlugin"
				}
				msg += " Check the crashes section in the settings for more info."

				Utils.showToast(msg, true)
				Looper.loop()
			}
			Thread.sleep(4200) // Wait for toast to end
			exitProcess(2)
		}

		// TODO: somehow save settings when changing the settings properties
		settings = Klaxon().parse<CoreSettings>(Constants.Paths.CORE_SETTINGS)
			?: CoreSettings()
		PluginManager.loadAllPlugins()
		PluginManager.startAllPlugins()
	}
}
