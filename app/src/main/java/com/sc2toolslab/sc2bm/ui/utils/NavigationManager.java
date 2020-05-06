package com.sc2toolslab.sc2bm.ui.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;
import com.sc2toolslab.sc2bm.ui.activities.BuildEditActivity;
import com.sc2toolslab.sc2bm.ui.activities.BuildMakerActivity;
import com.sc2toolslab.sc2bm.ui.activities.BuildMakerAddItemActivity;
import com.sc2toolslab.sc2bm.ui.activities.BuildPlayerActivity;
import com.sc2toolslab.sc2bm.ui.activities.BuildViewActivity;
import com.sc2toolslab.sc2bm.ui.activities.MainActivity;
import com.sc2toolslab.sc2bm.ui.activities.OnlineLibraryActivity;
import com.sc2toolslab.sc2bm.ui.activities.SettingsActivity;

public class NavigationManager {
	private NavigationManager() {
	}

	public static void startMainActivity(Activity from) {
		Intent i = new Intent(from, MainActivity.class);
		from.startActivity(i);
		//from.finish();
	}

	public static void startSettingsActivity(Activity from) {
		Intent i = new Intent(from, SettingsActivity.class);
		from.startActivity(i);
		//from.finish();
	}

	public static void startBuildViewActivity(Activity from, String buildName) {
		Intent i = new Intent(from, BuildViewActivity.class);
		i.putExtra(AppConstants.BUILD_ORDER_NAME_INTENT_KEY, buildName);
		from.startActivity(i);
	}

	public static void startBuildEditActivity(Activity from, String buildName) {
		Intent i = new Intent(from, BuildEditActivity.class);
		i.putExtra(AppConstants.BUILD_ORDER_NAME_INTENT_KEY, buildName);
		from.startActivityForResult(i, 1);
	}

	public static void startBuildMakerActivity(Activity from, String buildName) {
		Intent i = new Intent(from, BuildMakerActivity.class);
		i.putExtra(AppConstants.BUILD_ORDER_NAME_INTENT_KEY, buildName);
		from.startActivity(i);
	}

	public static void startBuildPlayerActivity(Activity from, String buildName) {
		Intent i = new Intent(from, BuildPlayerActivity.class);
		i.putExtra(AppConstants.BUILD_ORDER_NAME_INTENT_KEY, buildName);
		from.startActivity(i);
	}

	public static void startBuildMakerAddItemActivity(Activity from, BuildItemTypeEnum itemType, int selectedItemIndex) {
		Intent i = new Intent(from, BuildMakerAddItemActivity.class);
		i.putExtra(AppConstants.BUILD_ITEM_TYPE_INTENT_FLAG, itemType.name());
		i.putExtra("SelectedItemPosition", selectedItemIndex);
		from.startActivityForResult(i, 1);
	}

	public static void startShareActivity(Activity from, Uri fileName) {
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_STREAM, fileName);
		from.startActivity(Intent.createChooser(intent, "Share build order via..."));
	}

	public static void startOnlineLibraryActivity(Activity from) {
		Intent i = new Intent(from, OnlineLibraryActivity.class);
		//i.putExtra(AppConstants.BUILD_ORDER_NAME_INTENT_KEY, buildName);
		from.startActivity(i);
	}
}
