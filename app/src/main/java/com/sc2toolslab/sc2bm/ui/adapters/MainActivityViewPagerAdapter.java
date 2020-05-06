package com.sc2toolslab.sc2bm.ui.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.fragments.MainContentListFragment;
import com.sc2toolslab.sc2bm.ui.fragments.MainContentTileFragment;

import java.util.ArrayList;

public class MainActivityViewPagerAdapter extends FragmentStatePagerAdapter {
	private ArrayList<BuildFilterTab> mTabs = new ArrayList<>();
	private boolean mTileView = true;

	public MainActivityViewPagerAdapter(FragmentManager fm, boolean isTileView) {
		super(fm);

		mTileView = isTileView;

		_loadTabs();
	}

	@Override
	public Fragment getItem(int position) {
		Fragment content;

		if (mTileView) {
			content = new MainContentTileFragment();
		} else {
			content = new MainContentListFragment();
		}

		Bundle args = new Bundle();
		args.putString("VsFaction", mTabs.get(position).VsFaction.name());
		content.setArguments(args);

		return content;
	}

	@Override
	public int getCount() {
		return mTabs.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return null;//mTabs.get(position).Title;
	}

	public int getIcon(int position) {
		return mTabs.get(position).IconId;
	}

	public void setTileView(boolean isTyle) {
		mTileView = isTyle;
	}

	private void _loadTabs() {
		mTabs.add(new BuildFilterTab(R.mipmap.ic_favorite_yes, RaceEnum.NotDefined, "Favorites", 0));
		mTabs.add(new BuildFilterTab(R.mipmap.ic_zerg_white, RaceEnum.Zerg, "Zerg", 1));
		mTabs.add(new BuildFilterTab(R.mipmap.ic_terran_white, RaceEnum.Terran, "Terran", 2));
		mTabs.add(new BuildFilterTab(R.mipmap.ic_protoss_white, RaceEnum.Protoss, "Protoss", 3));
	}

	public class BuildFilterTab {
		public int IconId;
		public RaceEnum VsFaction;
		public String Title;
		public int Order;

		public BuildFilterTab(int iconId, RaceEnum vsFaction, String title, int order) {
			IconId = iconId;
			VsFaction = vsFaction;
			Title = title;
			Order = order;
		}
	}
}
