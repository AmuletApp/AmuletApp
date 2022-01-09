package com.github.redditvanced.core

import android.os.Environment
import com.github.redditvanced.core.util.FrontpageSettings

@Suppress("MemberVisibilityCanBePrivate", "unused")
class Constants {
	companion object {
		const val PROJECT_NAME = "RedditVanced"

		/**
		 * Github repository homepage
		 */
		const val GITHUB = "https://github.com/$PROJECT_NAME/$PROJECT_NAME"

		val APP_VERSION_CODE: Int = FrontpageSettings.i.appVersionCode
		val APP_VERSION_NAME: String = FrontpageSettings.i.appVersionName
	}

	object Paths {
		/**
		 * Reddit Vanced data path
		 */
		val BASE = Environment.getExternalStorageDirectory().absolutePath + "/$PROJECT_NAME"
		val PLUGINS = "$BASE/plugins"
		val THEMES = "$BASE/themes"
		val CRASHLOGS = "$BASE/crashlogs"

		/**
		 * Plugin extension
		 * TODO: find a better extension
		 */
		const val PLUGIN_EXT = ".rvp"
	}

	object Discord {
		// TODO: update server info
		const val SERVER_ID = 0L
		const val INVITE = ""

		object Channels {
			const val GENERAL = 0L
			const val THEME_DEV = 0L
			const val PLUGIN_DEV = 0L
		}
	}

	object Settings {
		// TODO: delegates
		const val CRASH_AUTO_DISABLE = "crashAutoDisable"
	}
}
