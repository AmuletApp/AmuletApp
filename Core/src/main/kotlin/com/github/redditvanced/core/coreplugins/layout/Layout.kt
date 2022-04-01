package com.github.redditvanced.core.coreplugins.layout

import com.github.redditvanced.core.coreplugins.CorePlugin

internal class Layout : CorePlugin("Layout", "Remove elements from the UI.") {
	private val patches = listOf<BaseLayoutPatch>(
		AntiPremium()
	)

	override fun onStart() {
		// TODO: toggle patches page

		patches.forEach {
			// TODO: enable/disable patches
			it.start(patcher)
		}
	}
}
