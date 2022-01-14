package com.reddit.frontpage.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.github.redditvanced.injector.Injector;
import com.reddit.frontpage.LightboxActivity;
import com.reddit.screen.media.streaming.StreamActivity;

// gg1.a is ActivityLifecycleCallbacksSimple
@SuppressWarnings("unused")
@SuppressLint("SourceLockedOrientationActivity")
public class AppActivity$a extends gg1.a {
	public AppActivity$a(MainActivity activity) {
		Injector.Companion.init(activity);
	}

	// TODO: investigate MainActivity, its empty

	@Override
	public void onActivityDestroyed(Activity activity) {
		if ((activity instanceof LightboxActivity) || (activity instanceof StreamActivity)) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	public void onActivityStarted(Activity activity) {
		if (activity instanceof LightboxActivity) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		} else if (activity instanceof StreamActivity) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
}
