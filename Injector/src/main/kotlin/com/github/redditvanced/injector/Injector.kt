package com.github.redditvanced.injector

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import android.util.Log
import android.widget.Toast
import dalvik.system.BaseDexClassLoader
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import top.canyie.pine.Pine
import top.canyie.pine.PineConfig
import top.canyie.pine.callback.MethodHook
import java.io.*
import java.util.zip.ZipFile

typealias BaseActivity = com.reddit.frontpage.a
typealias FrontpageSettings = xw.c

@ExperimentalSerializationApi
class Injector {
	companion object {
		const val LOG_TAG = "Injector"
		private const val PROJECT_NAME = "RedditVanced"
		private const val USE_LOCAL_CORE_KEY = "RV_useCustomCore"
		private val BASE_DIRECTORY = File(Environment.getExternalStorageDirectory().absolutePath, PROJECT_NAME)

		private var baseActivityUnhook: MethodHook.Unhook? = null
		fun init(activity: BaseActivity) {
			PineConfig.debug = File(BASE_DIRECTORY, ".pine_debug").exists()
			PineConfig.debuggable = File(BASE_DIRECTORY, ".debuggable").exists()
			Log.d(LOG_TAG, "Debuggable: ${PineConfig.debuggable}")
			PineConfig.disableHiddenApiPolicy = false
			PineConfig.disableHiddenApiPolicyForPlatformDomain = false
			Pine.disableJitInline()
			Pine.disableProfileSaver()

			// Pine's disableHiddenApiPolicy crashes according to Juby
			HiddenAPIPolicy.disableHiddenApiPolicy()

			try {
				// baseActivityUnhook = Pine.hook(
				// 	BaseActivity::class.java.getDeclaredMethod("onCreate", Bundle::class.java),
				// 	object : MethodHook() {
				// 		override fun beforeCall(callFrame: Pine.CallFrame) {
				// 			initAliucord(callFrame.thisObject as BaseActivity)
				// 			baseActivityUnhook?.unhook()
				// 			baseActivityUnhook = null
				// 		}
				// 	})
				initAliucord(activity)
			} catch (t: Throwable) {
				Log.e(LOG_TAG, "Failed to initialize Aliucord", t)
			}
		}

		// TODO: use proper appactivity
		fun initAliucord(activity: BaseActivity) {
			Log.i(LOG_TAG, "Initializing Aliucord...")
			if (!pruneArtProfile(activity))
				Log.w(LOG_TAG, "Failed to prune art profile")

			val prefs = activity.getSharedPreferences(PROJECT_NAME.lowercase(), Context.MODE_PRIVATE)
			val cachedCoreFile = File(activity.codeCacheDir, "$PROJECT_NAME.zip")

			// TODO: use json settings for internals instead
			val useCustomCore = prefs.getBoolean(USE_LOCAL_CORE_KEY, false)
			val customCoreFile = File(BASE_DIRECTORY, "$PROJECT_NAME.zip")

			try {
				val coreFile = if (useCustomCore && customCoreFile.exists()) {
					Log.d(LOG_TAG, "Loading custom core from ${customCoreFile.absolutePath}")
					customCoreFile
				} else if (useCustomCore) {
					Log.i(LOG_TAG, "Custom core missing, using default...")
					cachedCoreFile
				} else cachedCoreFile

				// Download default core to cache dir
				if (!useCustomCore && !cachedCoreFile.exists()) {
					Log.d(LOG_TAG, "Downloading core from github...")
					val thread = Thread {
						// TODO: download core
					}
					thread.start()
					thread.join()
					Log.d(LOG_TAG, "Finished downloading core from github...")

					val clientVersion = FrontpageSettings.i.appVersionCode
					Log.d(LOG_TAG, "Retrieved local Reddit version: $clientVersion")

					val zip = ZipFile(coreFile)
					val manifestStream = zip.getInputStream(zip.getEntry("manifest.json"))
					val manifest = Json.decodeFromStream<CoreManifest>(manifestStream)
					zip.close()
					Log.d(LOG_TAG, "Retrieved supported Reddit version: ${manifest.redditVersionCode}")

					if (manifest.redditVersionCode > clientVersion) {
						errorToast("Your base Reddit is outdated. Please reinstall using the Installer", activity, null)
					}
				}

				Log.d(LOG_TAG, "Adding Aliucord to the classpath...")
				addDexToClasspath(cachedCoreFile, activity.classLoader)
				Log.d(LOG_TAG, "Successfully added Aliucord to the classpath")

				val c = Class.forName("com.aliucord.Main")
				val preInit = c.getDeclaredMethod("preInit", BaseActivity::class.java)
				val init = c.getDeclaredMethod("init", BaseActivity::class.java)
				Log.d(LOG_TAG, "Invoking main Aliucord entry point...")
				preInit.invoke(null, activity)
				init.invoke(null, activity)
				Log.d(LOG_TAG, "Finished initializing Aliucord")
			} catch (t: Throwable) {
				// Delete file so it is reinstalled the next time
				cachedCoreFile.delete()

				errorToast("Failed to initialize $PROJECT_NAME", activity, t)
			}
		}

		private fun errorToast(msg: String, activity: BaseActivity, throwable: Throwable? = null) {
			Log.e(LOG_TAG, msg, throwable)
			Handler(Looper.getMainLooper()).post {
				Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
			}
		}

		@SuppressLint("DiscouragedPrivateApi") // this private api seems to be stable, thanks to facebook who use it in the facebook app
		private fun addDexToClasspath(dex: File, classLoader: ClassLoader) {
			// https://android.googlesource.com/platform/libcore/+/58b4e5dbb06579bec9a8fc892012093b6f4fbe20/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java#59
			val pathListField = BaseDexClassLoader::class.java.getDeclaredField("pathList")
			pathListField.isAccessible = true
			val pathList = pathListField[classLoader]!!
			val addDexPath = pathList.javaClass.getDeclaredMethod("addDexPath", String::class.java, File::class.java)
			addDexPath.isAccessible = true
			addDexPath.invoke(pathList, dex.absolutePath, null as File?)
		}

		/**
		 * Try to prevent method inlining by deleting the usage profile used by AOT compilation
		 * https://source.android.com/devices/tech/dalvik/configure#how_art_works
		 */
		private fun pruneArtProfile(ctx: Context): Boolean {
			Log.d(LOG_TAG, "Pruning ART usage profile...")
			val profile = File("/data/misc/profiles/cur/0/" + ctx.packageName + "/primary.prof")
			if (!profile.exists()) {
				return false
			}
			if (profile.length() > 0) {
				try {
					// Delete file contents
					FileOutputStream(profile).close()
				} catch (ignored: Throwable) {
					return false
				}
			}
			return true
		}
	}
}
