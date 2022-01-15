package com.github.redditvanced.injector

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.beust.klaxon.Klaxon
import com.github.redditvanced.common.*
import com.github.redditvanced.common.Constants.PROJECT_NAME
import com.github.redditvanced.common.models.CoreManifest
import com.reddit.frontpage.main.MainActivity
import dalvik.system.BaseDexClassLoader
import top.canyie.pine.Pine
import top.canyie.pine.PineConfig
import top.canyie.pine.callback.MethodHook
import java.io.*
import java.util.zip.ZipFile

object Injector {
	const val LOG_TAG = "Injector"

	// TODO: use json settings instead
	private const val USE_LOCAL_CORE_KEY = "RV_useCustomCore"

	// private var baseActivityUnhook: MethodHook.Unhook? = null
	fun init(activity: BaseActivity) {
		checkPermissions(activity)

		PineConfig.debug = File(Constants.Paths.BASE, ".pine_debug").exists()
		PineConfig.debuggable = File(Constants.Paths.BASE, ".debuggable").exists()
		Log.d(LOG_TAG, "Debuggable: ${PineConfig.debuggable}")
		PineConfig.disableHiddenApiPolicy = false
		PineConfig.disableHiddenApiPolicyForPlatformDomain = false
		Pine.disableJitInline()
		Pine.disableProfileSaver()

		// Pine's disableHiddenApiPolicy crashes according to Juby
		HiddenAPIPolicy.disableHiddenApiPolicy()

		Log.i(LOG_TAG, "Initializing $PROJECT_NAME...")
		try {
			// baseActivityUnhook = Pine.hook(
			// 	BaseActivity::class.java.getDeclaredMethod("onCreate", Bundle::class.java),
			// 	object : MethodHook() {
			// 		override fun beforeCall(callFrame: Pine.CallFrame) {
			// 			initCore(callFrame.thisObject as BaseActivity)
			// 			baseActivityUnhook?.unhook()
			// 			baseActivityUnhook = null
			// 		}
			// 	})
			initCore(activity)
		} catch (t: Throwable) {
			Log.e(LOG_TAG, "Failed to initialize $PROJECT_NAME", t)
		}
	}

	private fun initCore(activity: BaseActivity) {
		if (!pruneArtProfile(activity))
			Log.w(LOG_TAG, "Failed to prune art profile")

		val prefs = activity.getSharedPreferences(PROJECT_NAME.lowercase(), Context.MODE_PRIVATE)
		val cachedCoreFile = File(activity.codeCacheDir, "$PROJECT_NAME.zip")

		// TODO: use json settings for internals instead
		val useCustomCore = prefs.getBoolean(USE_LOCAL_CORE_KEY, false)
		val customCoreFile = File(Constants.Paths.BASE, "$PROJECT_NAME.zip")

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
				val manifest = Klaxon().parse<CoreManifest>(manifestStream)
					?: throw IllegalStateException("Failed to parse core manifest")
				zip.close()
				Log.d(LOG_TAG, "Retrieved supported Reddit version: ${manifest.redditVersionCode}")

				if (manifest.redditVersionCode > clientVersion) {
					errorToast("Your base Reddit is outdated. Please reinstall using the Installer", activity, null)
				}
			}

			Log.d(LOG_TAG, "Adding $PROJECT_NAME to the classpath...")
			addDexToClasspath(cachedCoreFile, activity.classLoader)
			Log.d(LOG_TAG, "Successfully added $PROJECT_NAME to the classpath")

			val c = Class.forName("com.github.redditvanced.core.Main")
			val init = c.getDeclaredMethod("init", BaseActivity::class.java)
			Log.d(LOG_TAG, "Invoking main $PROJECT_NAME entry point...")
			init.invoke(null, activity)
			Log.d(LOG_TAG, "Finished initializing $PROJECT_NAME")
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

	/**
	 * Check and request write storage permissions
	 * TODO: test if this works
	 */
	private fun checkPermissions(activity: BaseActivity): Boolean {
		val perm = Manifest.permission.WRITE_EXTERNAL_STORAGE

		// if (ContextCompat.a(activity, perm) == PackageManager.PERMISSION_GRANTED)
		// 	return
		if (activity.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED) return true

		Pine.hook(
			Fragment::class.java.getDeclaredMethod("onRequestPermissionsResult", Integer.TYPE, Array<String>::class.java, Integer.TYPE),
			object : MethodHook() {
				override fun beforeCall(callFrame: Pine.CallFrame) {
					val requestCode = callFrame.args[0] as Int
					val grantResults = callFrame.args[2] as Array<*>

					if (requestCode == 45987) {
						if (grantResults.isEmpty() || grantResults.contains(PackageManager.PERMISSION_DENIED))
							Toast.makeText(activity, "You have to grant storage permissions to use $PROJECT_NAME", Toast.LENGTH_LONG).show()
						else {
							Log.i(LOG_TAG, "MANAGE_EXTERNAL_STORAGE granted, restarting $PROJECT_NAME")
							val intent = Intent(activity, MainActivity::class.java)
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
							activity.startActivity(intent)
							Runtime.getRuntime().exit(0)
						}
						callFrame.result = null
					}
				}
			})

		activity.requestPermissions(arrayOf(perm), 45987)

		return false
	}
}
