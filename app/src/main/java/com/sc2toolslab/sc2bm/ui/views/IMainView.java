package com.sc2toolslab.sc2bm.ui.views;

import android.content.Intent;

public interface IMainView extends IView {
	void updateActionBar();
	void setNavigationDrawerVisible(boolean visible);
	boolean safeRunActivity(Intent activity);
	void showDialog(String title, int messageResourceId);
	void showMessage(int messageResourceId);
	void showAppSettings();
}