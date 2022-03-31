package com.github.redditvanced.injector

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.beust.klaxon.Klaxon
import com.github.redditvanced.common.Constants.PROJECT_NAME
import com.github.redditvanced.common.Constants.Paths
import com.github.redditvanced.common.FrontpageSettings
import com.github.redditvanced.common.models.CoreManifest
import com.github.redditvanced.common.models.CoreSettings
import com.github.redditvanced.common.toJsonFile
import com.reddit.frontpage.main.MainActivity
import com.reddit.frontpage.ui.HomePagerScreen
import dalvik.system.BaseDexClassLoader
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.Unhook
import de.robv.android.xposed.XposedBridge
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object Injector {
	private const val BASE_URL = "https://redditvanced.ddns.net/maven/releases/com/github/redditvanced/Core"
	private const val TAG = "Injector"
	private val klaxon = Klaxon()

	fun preInit() {
		val mOnCreate = MainActivity::class.java.getDeclaredMethod("onCreate", Bundle::class.java)
		var unhook: Unhook? = null
		unhook = XposedBridge.hookMethod(mOnCreate, object : XC_MethodHook() {
			override fun beforeHookedMethod(frame: MethodHookParam) {
				init(frame.thisObject as MainActivity)
				unhook!!.unhook()
				unhook = null
			}
		})
	}

	fun init(activity: MainActivity) {
		if (!XposedBridge.disableProfileSaver())
			Log.e(TAG, "Failed to disable profile saver")

		if (!XposedBridge.disableHiddenApiRestrictions())
			Log.e(TAG, "Failed to disable hidden api restrictions")

		if (!requestPermissions(activity))
			return

		Log.i(TAG, "Initializing $PROJECT_NAME...")
		try {
			initCore(activity)
		} catch (t: Throwable) {
			errorToast("Failed to initialize $PROJECT_NAME", activity, t)
		}
	}

	private fun initCore(activity: MainActivity) {
		if (!pruneArtProfile(activity))
			Log.w(TAG, "Failed to prune ART profile!")

		File(Paths.PLUGINS).mkdirs()
		File(Paths.THEMES).mkdir()
		File(Paths.CRASHLOGS).mkdir()

		if (!Paths.CORE_SETTINGS.exists()) {
			Paths.CORE_SETTINGS.createNewFile()
			klaxon.toJsonFile(CoreSettings(), Paths.CORE_SETTINGS)
		}

		val cachedCoreFile = File(activity.codeCacheDir, "core.zip")
		val coreSettings = requireNotNull(klaxon.parse<CoreSettings>(Paths.CORE_SETTINGS)) {
			"Failed to parse core settings!"
		}

		try {
			val coreFile = if (coreSettings.useCustomCore && Paths.CUSTOM_CORE.exists()) {
				Log.d(TAG, "Loading custom core from ${Paths.CUSTOM_CORE.absolutePath}")
				Paths.CUSTOM_CORE
			} else if (coreSettings.useCustomCore) {
				Log.w(TAG, "Custom core missing, using default...")
				cachedCoreFile
			} else cachedCoreFile

			// Download default core to cache dir
			if (!coreSettings.useCustomCore && !cachedCoreFile.exists()) {
				Log.d(TAG, "Downloading core from github...")

				var error: Throwable? = null
				cachedCoreFile.exists() || cachedCoreFile.createNewFile()
				Thread { downloadCore(cachedCoreFile) }.apply {
					setUncaughtExceptionHandler { _, t -> error = t }
					start()
					join()
				}
				if (error != null) {
					errorToast("Failed to download core zip!", activity, error)
					return
				}

				// TODO: remove this reflection once dex access changer is implemented
				val fInstance = FrontpageSettings::class.java.getDeclaredField("i")
					.apply { isAccessible = true }
				val clientVersion = (fInstance.get(null) as FrontpageSettings).appVersionCode

				// val clientVersion = FrontpageSettings.i.appVersionCode
				Log.d(TAG, "Local Reddit version: $clientVersion")

				val zip = ZipFile(coreFile)
				val manifestStream = zip.getInputStream(zip.getEntry("manifest.json"))
				val manifest = klaxon.parse<CoreManifest>(manifestStream)
					?: throw IllegalStateException("Failed to parse core manifest")
				zip.close()
				Log.d(TAG, "Retrieved supported Reddit version: ${manifest.redditVersionCode}")

				if (manifest.redditVersionCode > clientVersion) {
					errorToast("Your Reddit is outdated! Please reinstall RedditVanced with the manager.", activity, null)
					return
				}
			}

			Log.d(TAG, "Adding core to the classpath...")
			addDexToClasspath(coreFile, activity.classLoader)
			Log.d(TAG, "Successfully added core to the classpath.")

			val c = Class.forName("com.github.redditvanced.core.Main")
			val init = c.getDeclaredMethod("init", MainActivity::class.java)
			Log.d(TAG, "Invoking main core entry point...")
			init.invoke(null, activity)
			Log.d(TAG, "Finished initializing core.")
		} catch (t: Throwable) {
			// Delete file so it is reinstalled the next time
			if (!coreSettings.useCustomCore)
				cachedCoreFile.delete()
			errorToast("Failed to load $PROJECT_NAME", activity, t)
		}
	}

	private fun errorToast(msg: String, activity: Activity, throwable: Throwable? = null) {
		Log.e(TAG, msg, throwable)
		Handler(Looper.getMainLooper()).post {
			Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
		}
	}

	@SuppressLint("DiscouragedPrivateApi") // this private api seems to be stable, thanks to facebook who use it in the facebook app
	private fun addDexToClasspath(dex: File, classLoader: ClassLoader) {
		// https://android.googlesource.com/platform/libcore/+/58b4e5dbb06579bec9a8fc892012093b6f4fbe20/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java#59
		val pathListField = BaseDexClassLoader::class.java.getDeclaredField("pathList")
			.apply { isAccessible = true }
		val pathList = pathListField[classLoader]!!
		val addDexPath = pathList.javaClass.getDeclaredMethod("addDexPath", String::class.java, File::class.java)
			.apply { isAccessible = true }
		addDexPath.invoke(pathList, dex.absolutePath, null)
	}

	/**
	 * Try to prevent method inlining by deleting the usage profile used by AOT compilation
	 * https://source.android.com/devices/tech/dalvik/configure#how_art_works
	 */
	private fun pruneArtProfile(activity: MainActivity): Boolean {
		Log.d(TAG, "Pruning ART usage profile...")
		val profile = File("/data/misc/profiles/cur/0/" + activity.packageName + "/primary.prof")
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

	private fun downloadCore(output: File) {
		val http = OkHttpClient()

		val versionRequest = Request.Builder()
			.url("$BASE_URL/maven-metadata.xml")
			.header("User-Agent", "RedditVanced Injector")
			.build()

		val versionBody = http.newCall(versionRequest)
			.execute()
			.takeIf { it.code() == 200 }
			?.body()
			?.string()
			?: throw Error("Failed to fetch core version!")

		val version = "<release>(.+?)</release>"
			.toRegex()
			.find(versionBody)
			?.groupValues
			?.get(1)
			?: throw Error("Failed to find version in maven-metadata!")

		Log.i(TAG, "Fetched core version: $version")

		val zipRequest = Request.Builder()
			.url("$BASE_URL/$version/Core-$version.zip")
			.header("User-Agent", "RedditVanced Injector")
			.build()

		val zipData = http.newCall(zipRequest)
			.execute()
			.takeIf { it.code() == 200 }
			?.body()
			?.bytes()
			?: throw Error("Failed to fetch core zip!")

		output.writeBytes(zipData)
		Log.i(TAG, "Downloaded core zip!")
	}

	// TODO: api >30??
	@SuppressLint("NewApi")
	private fun requestPermissions(injectorActivity: MainActivity): Boolean {
		if (Environment.isExternalStorageManager())
			return true

		val mHomePage = HomePagerScreen::class.java.getDeclaredConstructor()
		val mOnResume = Activity::class.java.getDeclaredMethod("onResume")
		val mActivityCreate = Activity::class.java.getDeclaredMethod("onCreate", Bundle::class.java)

		// TODO: find way to get activity from HomePagerScreen w/o this
		var appActivity: Activity? = null
		val activityUnpatch = XposedBridge.hookMethod(mActivityCreate, object : XC_MethodHook() {
			override fun afterHookedMethod(frame: MethodHookParam) {
				appActivity = frame.thisObject as Activity
			}
		})

		var homePageUnpatch: Unhook? = null
		homePageUnpatch = XposedBridge.hookMethod(mHomePage, object : XC_MethodHook() {
			override fun afterHookedMethod(frame: MethodHookParam) {
				homePageUnpatch!!.unhook()
				activityUnpatch!!.unhook()

				var onResumeUnpatch: Unhook? = null
				onResumeUnpatch = XposedBridge.hookMethod(mOnResume, object : XC_MethodHook() {
					override fun afterHookedMethod(frame: MethodHookParam) {
						val activity = frame.thisObject as Activity
						onResumeUnpatch!!.unhook()

						if (Environment.isExternalStorageManager()) {
							val intent = activity.packageManager.getLaunchIntentForPackage(activity.packageName)
							activity.startActivity(Intent.makeRestartActivityTask(intent!!.component))
							exitProcess(0)
						} else thread(true) {
							errorToast("Manage storage permissions are required for RedditVanced to load!", activity)
							Thread.sleep(4200)
							errorToast("Please go into your settings and enable it.", activity)
						}
					}
				})

				val intent = Intent(
					Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
					Uri.parse("package:${injectorActivity.packageName}")
				)
				appActivity!!.startActivity(intent)
				appActivity = null
			}
		})

		return false
	}
}
