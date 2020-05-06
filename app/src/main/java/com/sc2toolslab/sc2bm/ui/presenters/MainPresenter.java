package com.sc2toolslab.sc2bm.ui.presenters;

import android.content.Intent;
import android.net.Uri;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.ui.adapters.NavDrawerMenuAdapter;
import com.sc2toolslab.sc2bm.ui.model.NavDrawerMenuItem;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.views.IMainView;

public class MainPresenter implements IPresenter, NavDrawerMenuAdapter.INavDrawerItemSelectedListener {
	private IMainView mView;

	private RaceEnum mCurrentFaction;

	public void onBuildSearch(String title) {
		BuildOrdersProvider.getInstance(mView.getContext()).setTitlePattern(title);
	}

	public RaceEnum getMainFaction() {
		return mCurrentFaction;
	}

	public MainPresenter(IMainView view) {
		mView = view;

		mCurrentFaction = BuildOrdersProvider.getInstance(mView.getContext()).getFactionFilter();
	}

	public void setMainFaction(RaceEnum faction) {
		if (mCurrentFaction != faction) {
			mCurrentFaction = faction;
			BuildOrdersProvider.getInstance(mView.getContext()).setFaction(mCurrentFaction);
			mView.updateActionBar();
		}
	}

	@Override
	public void onItemSelected(NavDrawerMenuItem item) {
		switch (item.Title.toLowerCase()) {
			case "buy" : {
				_openMarketPage(AppConstants.FULL_APP_NAME);
				break;
			}
			case "rate" : {
				String appName;

				if (AppConstants.IS_FREE) {
					appName = AppConstants.FREE_APP_NAME;
				} else {
					appName = AppConstants.FULL_APP_NAME;
				}

				_openMarketPage(appName);

				break;
			}
			case "sc2bm.com" : {
				_openWebSite();
				break;
			}
			case "feedback" : {
				_openEmailClient("SC2 BuildMaker feedback");
				break;
			}
			case "settings" : {
				mView.showAppSettings();
				break;
			}
			case "about" : {
				mView.showDialog("About...", R.string.dialog_about_message);
				break;
			}
		}

		mView.setNavigationDrawerVisible(false);
	}

	private void _openMarketPage(String appId) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		//Try Google play
		intent.setData(Uri.parse("market://details?id=" + appId));
		if (!mView.safeRunActivity(intent)) {
			//Market (Google play) app seems not installed, let's try to open a webbrowser
			intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + appId));
			if (!mView.safeRunActivity(intent)) {
				//Well if this also fails, we have run out of options, inform the user.
				mView.showMessage(R.string.notification_appmarket_not_installed);
			}
		}
	}

	private void _openWebSite() {
		Intent intent = new Intent(Intent.ACTION_VIEW);

		intent.setData(Uri.parse("http://sc2bm.com"));
		if (!mView.safeRunActivity(intent)) {
			//Well if this also fails, we have run out of options, inform the user.
			return;
		}
	}

	private void _openEmailClient(String subject) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("plain/text");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {AppConstants.FEEDBACK_EMAIL_ADDRESS});
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		mView.safeRunActivity(Intent.createChooser(intent, "Send..."));
	}
}