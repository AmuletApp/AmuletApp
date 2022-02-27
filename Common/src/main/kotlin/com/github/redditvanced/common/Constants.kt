package com.github.redditvanced.common

import android.os.Environment
import java.io.File

@Suppress("MemberVisibilityCanBePrivate", "unused")
object Constants {
	const val PROJECT_NAME = "RedditVanced"

	/**
	 * Github repository homepage
	 */
	const val GITHUB = "https://github.com/$PROJECT_NAME/$PROJECT_NAME"

	/**
	 * Discord support server
	 */
	const val DISCORD = "https://discord.gg/yZGanTPdPF"

	val APP_VERSION_CODE: Int = FrontpageSettings.i.appVersionCode
	val APP_VERSION_NAME: String = FrontpageSettings.i.appVersionName

	@Suppress("DEPRECATION")
	object Paths {
		val BASE = Environment.getExternalStorageDirectory().absolutePath + "/$PROJECT_NAME"
		val PLUGINS = "$BASE/plugins"
		val THEMES = "$BASE/themes"
		val CRASHLOGS = "$BASE/crashlogs"

		const val PLUGIN_EXT = ".rvanced"
		val CORE_SETTINGS = File("$BASE/settings.json")
		val CUSTOM_CORE = File("$BASE/build/core.zip")
		val INJECTOR = File("$BASE/build/injector.zip")
	}
}
