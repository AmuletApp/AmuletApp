package com.github.redditvanced.core.coreplugins.layout

import android.view.View
import android.widget.FrameLayout
import com.github.redditvanced.core.patcher.Patcher
import com.github.redditvanced.core.util.Utils
import com.reddit.frontpage.`R$id`.toolbar_nav_search_cta_coins_container

internal class AntiPremium : BaseLayoutPatch() {
	override val name = "AntiPremium"
	override val description = "Remove traces of premium & coins elements."
	override val requiresRestart = false

	override fun start(patcher: Patcher) {

		val patch = {
			val activity = Utils.appActivity

			activity.findViewById<FrameLayout>(toolbar_nav_search_cta_coins_container)
				.visibility = View.GONE
		}

		Utils.mainThread.postDelayed(patch, 1500)
	}
}
