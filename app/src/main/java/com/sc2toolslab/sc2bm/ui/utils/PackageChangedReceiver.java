package com.sc2toolslab.sc2bm.ui.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sc2toolslab.sc2bm.dataaccess.FileStorageHelper;

public class PackageChangedReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context ctx, Intent intent) {
		FileStorageHelper.checkDirectories(ctx);
	}
}