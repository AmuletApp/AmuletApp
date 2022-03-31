/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210
 * Licensed under the Open Software License version 3.0
 */

package com.github.redditvanced.core.util

@Suppress("unused")
object DimenUtils {
	private val density = Utils.appContext.resources.displayMetrics.density

	/**
	 * Converts DP to PX
	 * @return `DP` converted to PX
	 */
	val Int.dp: Int
		get() = dpToPx(this)

	/**
	 * Converts DP to PX.
	 * @param dp DP value
	 * @return `DP` converted to PX
	 */
	@JvmStatic
	fun dpToPx(dp: Int): Int = dpToPx(dp.toFloat())

	/**
	 * Converts DP to PX.
	 * @param dp DP value
	 * @return `DP` converted to PX
	 */
	@JvmStatic
	fun dpToPx(dp: Float): Int = (dp * density + 0.5f).toInt()
}
