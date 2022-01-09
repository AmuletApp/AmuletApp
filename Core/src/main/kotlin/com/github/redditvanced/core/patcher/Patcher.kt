package com.github.redditvanced.core.patcher

import com.github.redditvanced.core.util.Logger
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Member

private typealias HookCallback<T> = T.(XC_MethodHook.MethodHookParam) -> Unit
private typealias InsteadHookCallback<T> = T.(XC_MethodHook.MethodHookParam) -> Any?
private typealias Unpatch = () -> Unit

@Suppress("unused")
class Patcher(
	/**
	 * The logger that errors while patching will be logged from
	 */
	val logger: Logger,
) {
	/**
	 * List of unpatches for all patches added.
	 * These will all be called once this PatcherAPI's lifecycle is ending.
	 */
	private val unpatches = mutableListOf<Unpatch>()

	// TODO: make constructor/method hooks use the member hook, reduce code duplication

	/**
	 * Replaces a constructor of a class.
	 * @param paramTypes parameters of the method. Useful for patching individual overloads
	 * @param callback callback for the patch
	 * @return The [Runnable] object of the patch
	 * @see [XC_MethodHook.beforeHookedMethod]
	 */
	inline fun <reified T> instead(vararg paramTypes: Class<*>, crossinline callback: InsteadHookCallback<T>): Unpatch =
		patch(T::class.java.getDeclaredConstructor(*paramTypes), object : XC_MethodHook() {
			override fun beforeHookedMethod(param: MethodHookParam) {
				try {
					param.result = callback(param.thisObject as T, param)
				} catch (th: Throwable) {
					logger.error("Exception while instead-hooking constructor of ${param.method.declaringClass}", th)
				}
			}
		})

	/**
	 * Replaces a method of a class.
	 * @param methodName name of the method to patch
	 * @param paramTypes parameters of the method. Useful for patching individual overloads
	 * @param callback callback for the patch
	 * @return The [Runnable] object of the patch
	 * @see [XC_MethodHook.beforeHookedMethod]
	 */
	inline fun <reified T> instead(methodName: String, vararg paramTypes: Class<*>, crossinline callback: InsteadHookCallback<T>): Unpatch =
		patch(T::class.java.getDeclaredMethod(methodName, *paramTypes), object : XC_MethodHook() {
			override fun beforeHookedMethod(param: MethodHookParam) {
				try {
					param.result = callback(param.thisObject as T, param)
				} catch (th: Throwable) {
					logger.error("Exception while instead-hooking ${param.method.declaringClass.name}.${param.method.name}", th)
				}
			}
		})

	/**
	 * Replaces a method/constructor of a class.
	 * @param member Method or constructor to patch. see [Member]
	 * @param callback callback for the patch
	 * @return The [Runnable] object of the patch
	 * @see [XC_MethodHook.beforeHookedMethod]
	 */
	inline fun instead(member: Member, crossinline callback: InsteadHookCallback<Any>): Unpatch =
		patch(member, object : XC_MethodHook() {
			override fun beforeHookedMethod(param: MethodHookParam) {
				try {
					param.result = callback(param.thisObject, param)
				} catch (th: Throwable) {
					logger.error("Exception while instead-hooking ${param.method.declaringClass.name}.${param.method.name}", th)
				}
			}
		})

	/**
	 * Pre-hooks a constructor of a class.
	 * @param paramTypes parameters of the constructor. Useful for patching individual overloads
	 * @param callback callback for the patch
	 * @return The [Runnable] object of the patch
	 * @see [XC_MethodHook.beforeHookedMethod]
	 */
	inline fun <reified T> before(vararg paramTypes: Class<*>, crossinline callback: HookCallback<T>): Unpatch =
		patch(T::class.java.getDeclaredConstructor(*paramTypes), object : XC_MethodHook() {
			override fun beforeHookedMethod(param: MethodHookParam) {
				try {
					callback(param.thisObject as T, param)
				} catch (th: Throwable) {
					logger.error("Exception while pre-hooking constructor of ${param.method.declaringClass}", th)
				}
			}
		})

	/**
	 * Pre-hooks a method of a class.
	 * @param methodName name of the method to patch
	 * @param paramTypes parameters of the method. Useful for patching individual overloads
	 * @param callback callback for the patch
	 * @return The [Runnable] object of the patch
	 * @see [XC_MethodHook.beforeHookedMethod]
	 */
	inline fun <reified T> before(methodName: String, vararg paramTypes: Class<*>, crossinline callback: HookCallback<T>): Unpatch =
		patch(T::class.java.getDeclaredMethod(methodName, *paramTypes), object : XC_MethodHook() {
			override fun beforeHookedMethod(param: MethodHookParam) {
				try {
					callback(param.thisObject as T, param)
				} catch (th: Throwable) {
					logger.error("Exception while pre-hooking ${param.method.declaringClass.name}.${param.method.name}", th)
				}
			}
		})

	/**
	 * Pre-hooks a method/constructor of a class.
	 * @param member Method or constructor to patch. see [Member]
	 * @param callback callback for the patch
	 * @return The [Runnable] object of the patch
	 * @see [XC_MethodHook.beforeHookedMethod]
	 */
	inline fun before(member: Member, crossinline callback: HookCallback<Any>): Unpatch =
		patch(member, object : XC_MethodHook() {
			override fun beforeHookedMethod(param: MethodHookParam) {
				try {
					callback(param.thisObject, param)
				} catch (th: Throwable) {
					logger.error("Exception while pre-hooking ${param.method.declaringClass.name}.${param.method.name}", th)
				}
			}
		})

	/**
	 * Post-hooks a constructor of a class.
	 * @param paramTypes parameters of the constructor. Useful for patching individual overloads
	 * @param callback callback for the patch
	 * @return the [Runnable] object of the patch
	 * @see [XC_MethodHook.afterHookedMethod]
	 */
	inline fun <reified T> after(vararg paramTypes: Class<*>, crossinline callback: HookCallback<T>): Unpatch =
		patch(T::class.java.getDeclaredConstructor(*paramTypes), object : XC_MethodHook() {
			override fun afterHookedMethod(param: MethodHookParam) {
				try {
					callback(param.thisObject as T, param)
				} catch (th: Throwable) {
					logger.error("Exception while hooking constructor of ${param.method.declaringClass}", th)
				}
			}
		})

	/**
	 * Post-hooks a method of a class.
	 * @param methodName name of the method to patch
	 * @param paramTypes parameters of the method. Useful for patching individual overloads
	 * @param callback callback for the patch
	 * @return the [Runnable] object of the patch
	 * @see [XC_MethodHook.afterHookedMethod]
	 */
	inline fun <reified T> after(methodName: String, vararg paramTypes: Class<*>, crossinline callback: HookCallback<T>): Unpatch =
		patch(T::class.java.getDeclaredMethod(methodName, *paramTypes), object : XC_MethodHook() {
			override fun afterHookedMethod(param: MethodHookParam) {
				try {
					callback(param.thisObject as T, param)
				} catch (th: Throwable) {
					logger.error("Exception while hooking ${param.method.declaringClass.name}.${param.method.name}", th)
				}
			}
		})

	/**
	 * Post-hooks a method of a class.
	 * @param member Method or constructor to patch. see [Member]
	 * @param callback callback for the patch
	 * @return the [Runnable] object of the patch
	 * @see [XC_MethodHook.afterHookedMethod]
	 */
	inline fun after(member: Member, crossinline callback: HookCallback<Any>): Unpatch =
		patch(member, object : XC_MethodHook() {
			override fun afterHookedMethod(param: MethodHookParam) {
				try {
					callback(param.thisObject, param)
				} catch (th: Throwable) {
					logger.error("Exception while hooking ${param.method.declaringClass.name}.${param.method.name}", th)
				}
			}
		})

	/**
	 * Patches a method or constructor.
	 * @param m Method or constructor to patch. see [Member]
	 * @param hook Callback for the patch
	 * @return Method that will remove the patch when invoked
	 */
	fun patch(m: Member, hook: XC_MethodHook): Unpatch {
		return createUnpatch(XposedBridge.hookMethod(m, hook))
	}

	/**
	 * Runs all unpatches created by this patcher.
	 */
	fun unpatchAll() =
		unpatches.forEach { it.invoke() }

	private fun createUnpatch(unhook: XC_MethodHook.Unhook): Unpatch {
		var unpatch: Unpatch? = null
		unpatch = {
			unhook.unhook()
			unpatches.remove(unpatch)
			Unit
		}
		unpatches.add(unpatch)
		return unpatch
	}
}
