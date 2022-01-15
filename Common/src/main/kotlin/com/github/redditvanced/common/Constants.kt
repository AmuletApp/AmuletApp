package com.github.redditvanced.common

import android.os.Environment

@Suppress("MemberVisibilityCanBePrivate", "unused")
object Constants {
	const val PROJECT_NAME = "RedditVanced"

	/**
	 * Github repository homepage
	 */
	const val GITHUB = "https://github.com/$PROJECT_NAME/$PROJECT_NAME"

	val APP_VERSION_CODE: Int = FrontpageSettings.i.appVersionCode
	val APP_VERSION_NAME: String = FrontpageSettings.i.appVersionName

	@Suppress("DEPRECATION")
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
}
