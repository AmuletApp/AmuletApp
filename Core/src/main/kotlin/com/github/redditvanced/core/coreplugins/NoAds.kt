package com.github.redditvanced.core.coreplugins

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.reddit.domain.model.ILink
import com.reddit.domain.model.listing.Listing
import com.reddit.frontpage.presentation.detail.CommentScreenAdView

class NoAds : CorePlugin("NoAds", "Remove advertisements and promoted posts from your feed.") {
	override fun onStart() {
		val cString = String::class.java

		// Ads under opened posts
		patcher.after<CommentScreenAdView>(Context::class.java, AttributeSet::class.java) {
			rootView.visibility = View.GONE
		}

		// Ads in feed
		patcher.before<Listing<*>>(
			List::class.java,
			cString,
			cString,
			cString,
			cString,
			Boolean::class.javaPrimitiveType!!
		) {
			val list = it.args[0] as List<*>

			if (list[0] !is ILink)
				return@before

			it.args[0] = list.filterNot { link ->
				(link as ILink).promoted
			}
		}
	}
}
