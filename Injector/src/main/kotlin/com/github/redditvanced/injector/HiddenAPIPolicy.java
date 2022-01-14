package com.github.redditvanced.injector;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Objects;

public class HiddenAPIPolicy {
	/**
	 * Disables the Android Hidden API by adding an exemption for everything ("L", the prefix of all class references, e.g. Ljava/lang/String)
	 * Works by getting reflection methods through reflection,
	 * then invoking those methods to get and invoke a setter for hidden API exemptions.
	 * This works because the VM thinks it's internals calling the hidden method,
	 * since we're invoking reflection methods using reflection.
	 * <p>
	 * See https://weishu.me/2019/03/16/another-free-reflection-above-android-p/ (chinese)
	 */
	public static void disableHiddenApiPolicy() {
		// Not supported as it doesn't exist
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return;

		try {
			var mForName = Class.class.getDeclaredMethod("forName", String.class);
			var mGetDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);

			// https://android.googlesource.com/platform/libcore/+/master/libart/src/main/java/dalvik/system/VMRuntime.java
			var cVMRuntime = mForName.invoke(null, "dalvik.system.VMRuntime");
			var mGetRuntime = (Method) mGetDeclaredMethod.invoke(cVMRuntime, "getRuntime", null);
			Objects.requireNonNull(mGetRuntime, "Failed to get getRuntime()!");

			var mSetHiddenApiExemptions = (Method) mGetDeclaredMethod.invoke(
				cVMRuntime,
				"setHiddenApiExemptions",
				new Class[]{ String[].class }
			);
			Objects.requireNonNull(mSetHiddenApiExemptions, "Failed to get setHiddenApiExemptions()!");

			var vmRuntime = mGetRuntime.invoke(null);
			Objects.requireNonNull(vmRuntime, "Failed to get VMRuntime!");
			mSetHiddenApiExemptions.invoke(vmRuntime, (Object) new String[]{ "L" });
		} catch (Exception t) {
			Log.e(Injector.LOG_TAG, "Failed to disable hidden api policy!", t);
		}
	}
}
