package com.github.redditvanced.core.coreplugins

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.redditvanced.core.util.DimenUtils.dp
import com.github.redditvanced.core.util.Utils
import com.github.redditvanced.core.util.ViewUtils.getChildAt
import com.instabug.all.`R$drawable`.ib_core_ic_report_bug
import com.reddit.frontpage.`R$id`.drawer_nav_items_container
import com.reddit.frontpage.`R$layout`.drawer_nav_item
import com.reddit.frontpage.debug.DebugActivity
import com.reddit.frontpage.main.MainActivity

@SuppressLint("SetTextI18n")
class DebugPage : CorePlugin("DebugPage", "Open the debug Reddit page from the home sidebar.") {
	override fun onStart() {
		Utils.threadPool.execute {
			Thread.sleep(2000)
			Utils.mainThread.post { patchLayout(Utils.appActivity) }
		}

		patcher.after<MainActivity>("onResume") {
			Utils.threadPool.execute {
				Thread.sleep(200)
				Utils.mainThread.post { patchLayout(this) }
			}
		}
	}

	private fun patchLayout(activity: Activity) {
		val item = activity.layoutInflater.inflate(drawer_nav_item, null) as ViewGroup

		// Item icon
		item.getChildAt<ImageView>(0).apply {
			setPadding(9.dp, 0, 0, 0)
			setImageResource(ib_core_ic_report_bug)
		}

		// Item title
		item.getChildAt<TextView>(1).text = "Debug Page"

		// Item subtext
		item.getChildAt(2).visibility = View.GONE

		item.setOnClickListener {
			Utils.appActivity.startActivity(Intent(Utils.appActivity, DebugActivity::class.java))
		}

		val items = activity.findViewById<LinearLayout>(drawer_nav_items_container)
		items.addView(item)
	}
}
