package com.github.redditvanced.core.patcher

import com.github.redditvanced.core.util.Logger

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
	val unpatches = listOf<() -> Unit>()

	// /**
	//  * Replaces a constructor of a class.
	//  * @param paramTypes parameters of the method. Useful for patching individual overloads
	//  * @param callback callback for the patch
	//  * @return The [Runnable] object of the patch
	//  * @see [XC_MethodHook.beforeHookedMethod]
	//  */
	// inline fun <reified T> instead(vararg paramTypes: Class<*>, crossinline callback: InsteadHookCallback<T>): Runnable =
	// 	patch(T::class.java.getDeclaredConstructor(*paramTypes), object : XC_MethodHook() {
	// 		override fun beforeHookedMethod(param: MethodHookParam) {
	// 			try {
	// 				param.result = callback(param.thisObject as T, param)
	// 			} catch (th: Throwable) {
	// 				logger.error("Exception while replacing constructor of ${param.method.declaringClass}", th)
	// 			}
	// 		}
	// 	})
	//
	// /**
	//  * Replaces a method of a class.
	//  * @param methodName name of the method to patch
	//  * @param paramTypes parameters of the method. Useful for patching individual overloads
	//  * @param callback callback for the patch
	//  * @return The [Runnable] object of the patch
	//  * @see [XC_MethodHook.beforeHookedMethod]
	//  */
	// inline fun <reified T> instead(methodName: String, vararg paramTypes: Class<*>, crossinline callback: InsteadHookCallback<T>): Runnable =
	// 	patch(T::class.java.getDeclaredMethod(methodName, *paramTypes), object : XC_MethodHook() {
	// 		override fun beforeHookedMethod(param: MethodHookParam) {
	// 			try {
	// 				param.result = callback(param.thisObject as T, param)
	// 			} catch (th: Throwable) {
	// 				logger.error("Exception while replacing ${param.method.declaringClass.name}.${param.method.name}", th)
	// 			}
	// 		}
	// 	})
	//
	// /**
	//  * Adds a [PreHook] to a constructor of a class.
	//  * @param paramTypes parameters of the constructor. Useful for patching individual overloads
	//  * @param callback callback for the patch
	//  * @return The [Runnable] object of the patch
	//  * @see [XC_MethodHook.beforeHookedMethod]
	//  */
	// inline fun <reified T> before(vararg paramTypes: Class<*>, crossinline callback: HookCallback<T>): Runnable =
	// 	patch(T::class.java.getDeclaredConstructor(*paramTypes), object : XC_MethodHook() {
	// 		override fun beforeHookedMethod(param: MethodHookParam) {
	// 			try {
	// 				callback(param.thisObject as T, param)
	// 			} catch (th: Throwable) {
	// 				logger.error("Exception while pre-hooking constructor of ${param.method.declaringClass}", th)
	// 			}
	// 		}
	// 	})
	//
	// /**
	//  * Adds a [PreHook] to a method of a class.
	//  * @param methodName name of the method to patch
	//  * @param paramTypes parameters of the method. Useful for patching individual overloads
	//  * @param callback callback for the patch
	//  * @return The [Runnable] object of the patch
	//  * @see [XC_MethodHook.beforeHookedMethod]
	//  */
	// inline fun <reified T> before(methodName: String, vararg paramTypes: Class<*>, crossinline callback: HookCallback<T>): Runnable =
	// 	patch(T::class.java.getDeclaredMethod(methodName, *paramTypes), object : XC_MethodHook() {
	// 		override fun beforeHookedMethod(param: MethodHookParam) {
	// 			try {
	// 				callback(param.thisObject as T, param)
	// 			} catch (th: Throwable) {
	// 				logger.error("Exception while pre-hooking ${param.method.declaringClass.name}.${param.method.name}", th)
	// 			}
	// 		}
	// 	})
	//
	// /**
	//  * Adds a [Hook] to a constructor of a class.
	//  * @param paramTypes parameters of the constructor. Useful for patching individual overloads
	//  * @param callback callback for the patch
	//  * @return the [Runnable] object of the patch
	//  * @see [XC_MethodHook.afterHookedMethod]
	//  */
	// inline fun <reified T> after(vararg paramTypes: Class<*>, crossinline callback: HookCallback<T>): Runnable =
	// 	patch(T::class.java.getDeclaredConstructor(*paramTypes), object : XC_MethodHook() {
	// 		override fun afterHookedMethod(param: MethodHookParam) {
	// 			try {
	// 				callback(param.thisObject as T, param)
	// 			} catch (th: Throwable) {
	// 				logger.error("Exception while hooking constructor of ${param.method.declaringClass}", th)
	// 			}
	// 		}
	// 	})
	//
	// /**
	//  * Adds a [Hook] to a method of a class.
	//  * @param methodName name of the method to patch
	//  * @param paramTypes parameters of the method. Useful for patching individual overloads
	//  * @param callback callback for the patch
	//  * @return the [Runnable] object of the patch
	//  * @see [XC_MethodHook.afterHookedMethod]
	//  */
	// inline fun <reified T> after(methodName: String, vararg paramTypes: Class<*>, crossinline callback: HookCallback<T>): Runnable =
	// 	patch(T::class.java.getDeclaredMethod(methodName, *paramTypes), object : XC_MethodHook() {
	// 		override fun afterHookedMethod(param: MethodHookParam) {
	// 			try {
	// 				callback(param.thisObject as T, param)
	// 			} catch (th: Throwable) {
	// 				logger.error("Exception while hooking ${param.method.declaringClass.name}.${param.method.name}", th)
	// 			}
	// 		}
	// 	})
}
