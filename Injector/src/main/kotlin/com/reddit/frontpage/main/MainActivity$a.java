package com.reddit.frontpage.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.github.redditvanced.injector.Injector;
import com.reddit.frontpage.LightboxActivity;
import com.reddit.screen.media.streaming.StreamActivity;

@SuppressWarnings("unused")
@SuppressLint("SourceLockedOrientationActivity")
public class MainActivity$a extends lG.a {
	private static boolean initialized = false;

	public MainActivity$a(MainActivity activity) {
		if (!initialized) {
			initialized = true;
			Injector.INSTANCE.preInit(activity);
		}
	}

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
