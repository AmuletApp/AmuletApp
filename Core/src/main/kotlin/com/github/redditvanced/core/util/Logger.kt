package com.github.redditvanced.core.util

import android.util.Log
import com.github.redditvanced.common.Constants

/**
 * Logger that will log to logcat
 * @param module Name of the module
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class Logger(var module: String) {
	private fun format(msg: Any) = "[$module] $msg"

	/**
	 * Logs a [Log.VERBOSE] message
	 * @param msg Message to log
	 */
	fun verbose(msg: Any) = Log.v(Constants.PROJECT_NAME, format(msg))

	/**
	 * Logs a [Log.DEBUG] message
	 * @param msg Message to log
	 */
	fun debug(msg: Any) = Log.d(Constants.PROJECT_NAME, format(msg))

	/**
	 * Logs a [Log.INFO] message and prints the stacktrace of the exception
	 * @param msg Message to log
	 * @param throwable Exception to log
	 */
	@JvmOverloads
	fun info(msg: Any, throwable: Throwable? = null) =
		Log.i(Constants.PROJECT_NAME, format(msg), throwable)

	/**
	 * Logs a [Log.INFO] message, and shows it to the user as a toast
	 * @param msg Message to log/show
	 */
	fun infoToast(msg: Any) {
		Utils.showToast(msg.toString())
		info(msg, null)
	}

	/**
	 * Logs a [Log.WARN] message and prints the stacktrace of the exception
	 * @param msg Message to log
	 * @param throwable Exception to log
	 */
	@JvmOverloads
	fun warn(msg: Any, throwable: Throwable? = null) =
		Log.w(Constants.PROJECT_NAME, format(msg), throwable)

	/**
	 * Logs a [Log.WARN] message, and shows it to the user as a toast
	 * @param msg Message to log/show
	 * @param throwable Exception to log
	 */
	fun warnToast(msg: Any, throwable: Throwable? = null) {
		Utils.showToast(msg)
		Log.w(Constants.PROJECT_NAME, format(msg), throwable)
	}

	/**
	 * Logs a [Log.ERROR] message and prints the stacktrace of the exception
	 * @param msg Message to log
	 * @param throwable Exception to log
	 */
	@JvmOverloads
	fun error(msg: Any, throwable: Throwable? = null) =
		Log.e(Constants.PROJECT_NAME, format(msg), throwable)

	/**
	 * Logs an exception
	 * @param throwable Exception to log
	 */
	fun error(throwable: Throwable?) =
		error("", throwable)

	/**
	 * Logs an exception and shows the user a generic error message
	 * @param throwable Exception to log
	 */
	fun errorToast(throwable: Throwable?) =
		errorToast("An error has occurred. Please check the log for more details.", throwable)

	/**
	 * Logs a [Log.ERROR] message, shows it to the user as a toast and prints the stacktrace of the exception
	 * @param msg Message to log/show
	 * @param throwable Exception to log
	 */
	@JvmOverloads
	fun errorToast(msg: Any, throwable: Throwable? = null) {
		Utils.showToast(msg.toString(), true)
		error(msg, throwable)
	}
}
