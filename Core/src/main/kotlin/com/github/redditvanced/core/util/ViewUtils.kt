package com.github.redditvanced.core.util

import android.view.View
import android.view.ViewGroup

@Suppress("unused")
object ViewUtils {
	/**
	 * Get a typed child of this ViewGroup at an index.
	 * @return Child at index or throws if non-existent/wrong type
	 */
	inline fun <reified T : View> ViewGroup.getChildAt(index: Int): T {
		val child = getChildAt(index)
			?: throw Exception("Child at $index does not exist!")

		if (child !is T)
			throw Exception("Unexpected child with type ${child.accessibilityClassName} at index $index!")

		return child
	}
}
