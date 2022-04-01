package com.github.redditvanced.core.coreplugins.layout

import com.github.redditvanced.core.patcher.Patcher
import com.github.redditvanced.core.patcher.Unpatch
import java.util.*

internal abstract class BaseLayoutPatch {
	abstract val name: String
	abstract val description: String
	abstract val requiresRestart: Boolean

	/**
	 * All the generated unpatches for this specific layout edit.
	 */
	val unpatches: LinkedList<Unpatch> = LinkedList()

	/**
	 * Called when this patch is enabled.
	 * @param patcher The [Layout] plugin's patcher
	 */
	abstract fun start(patcher: Patcher)

	/**
	 * Shortcut for adding an unpatch to the [BaseLayoutPatch.unpatches] list.
	 *
	 * Example:
	 * ```kotlin
	 * !patcher.after<xxx>("abc") {}
	 * ```
	 */
	private operator fun Unpatch.not() {
		unpatches.add(this)
	}
}
