package com.sc2toolslab.sc2bm.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.adapters.NavDrawerMenuAdapter;
import com.sc2toolslab.sc2bm.ui.model.MenuManager;
import com.sc2toolslab.sc2bm.ui.presenters.NavDrawerPresenter;
import com.sc2toolslab.sc2bm.ui.providers.FactionImageProvider;
import com.sc2toolslab.sc2bm.ui.utils.FloatingActionButton;
import com.sc2toolslab.sc2bm.ui.utils.UiDataViewHelper;
import com.sc2toolslab.sc2bm.ui.views.INavDrawerView;

public class NavDrawerFragment extends Fragment implements INavDrawerView {
	private ActionBarDrawerToggle mDrawerToggle;
	private ImageView mImgFaction;
	private TextView mTxtVsTerran;
	private TextView mTxtVsProtoss;
	private TextView mTxtVsZerg;
	private TextView mTxtVersion;
	private TextView mTxtAddon;
	private LinearLayout mImgFactionLayout;
	private NavDrawerMenuAdapter mMenuAdapter;

	private NavDrawerPresenter mPresenter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_nav_drawer, container, false);
		RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.lstItems);

		mImgFaction = (ImageView) layout.findViewById(R.id.imgFaction);
		mTxtVsTerran = (TextView) layout.findViewById(R.id.txtVsTerran);
		mTxtVsProtoss = (TextView) layout.findViewById(R.id.txtVsProtoss);
		mTxtVsZerg = (TextView) layout.findViewById(R.id.txtVsZerg);
		mTxtVersion = (TextView) layout.findViewById(R.id.txtVersion);
		mTxtAddon = (TextView) layout.findViewById(R.id.txtAddon);
		mImgFactionLayout = (LinearLayout) layout.findViewById(R.id.imgFactionLayout);

		mMenuAdapter = new NavDrawerMenuAdapter(getActivity(), MenuManager.getMenuItems());

		recyclerView.setAdapter(mMenuAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		mPresenter = new NavDrawerPresenter(this);

		return layout;
	}

	public void setUp(DrawerLayout drawerLayout, final Toolbar toolbar, final FloatingActionButton btnNew, NavDrawerMenuAdapter.INavDrawerItemSelectedListener listener) {
		if (listener != null) {
			mMenuAdapter.setClickListener(listener);
		}

		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

				getActivity().invalidateOptionsMenu();
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);

				getActivity().invalidateOptionsMenu();

				btnNew.setVisibility(View.VISIBLE);
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, slideOffset);

				if (btnNew.getVisibility() == View.VISIBLE) {
					btnNew.setVisibility(View.GONE);
				}

				// Fade ActionBar effect
				if (slideOffset < 0.6) {
					toolbar.setAlpha(1 - slideOffset);
				}
			}
		};

		drawerLayout.setDrawerListener(mDrawerToggle);

		drawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});
	}

	@Override
	public void setFaction(RaceEnum faction) {
		if(!isAdded()) {
			return;
		}

		mImgFaction.setImageResource(new FactionImageProvider().getImageResourceIdByKey(faction));

		if (faction == RaceEnum.Terran) {
			mImgFactionLayout.setBackgroundColor(getResources().getColor(R.color.terranBuild));
		} else if (faction == RaceEnum.Protoss) {
			mImgFactionLayout.setBackgroundColor(getResources().getColor(R.color.protossBuild));
		} else if (faction == RaceEnum.Zerg) {
			mImgFactionLayout.setBackgroundColor(getResources().getColor(R.color.zergBuild));
		} else if (faction == RaceEnum.NotDefined) {
			mImgFactionLayout.setBackgroundColor(getResources().getColor(R.color.randomBuild));
		}
	}

	@Override
	public void setVsTerranBuildCount(int count, RaceEnum mainFaction) {
		if(!isAdded()) {
			return;
		}

		String mainFactionLetter = UiDataViewHelper.getFactionLetter(mainFaction);
		String vsFactionLetter = UiDataViewHelper.getFactionLetter(RaceEnum.Terran);

		mTxtVsTerran.setText("- (" + count + ") " + mainFactionLetter + "v" + vsFactionLetter);
	}

	@Override
	public void setVsProtossBuildCount(int count, RaceEnum mainFaction) {
		if(!isAdded()) {
			return;
		}

		String mainFactionLetter = UiDataViewHelper.getFactionLetter(mainFaction);
		String vsFactionLetter = UiDataViewHelper.getFactionLetter(RaceEnum.Protoss);

		mTxtVsProtoss.setText("- (" + count + ") " + mainFactionLetter + "v" + vsFactionLetter);
	}

	@Override
	public void setVsZergBuildCount(int count, RaceEnum mainFaction) {
		if(!isAdded()) {
			return;
		}

		String mainFactionLetter = UiDataViewHelper.getFactionLetter(mainFaction);
		String vsFactionLetter = UiDataViewHelper.getFactionLetter(RaceEnum.Zerg);

		mTxtVsZerg.setText("- (" + count + ") " + mainFactionLetter + "v" + vsFactionLetter);
	}

	@Override
	public void setAddon(String version) {
		if(!isAdded()) {
			return;
		}

		mTxtAddon.setText("Addon: " + _getAddonByVersion(version).toUpperCase());
	}

	@Override
	public void setVersion(String version) {
		if(!isAdded()) {
			return;
		}

		mTxtVersion.setText("Version: " + version);
	}

	private String _getAddonByVersion(String version) {
		switch (version) {
			case AppConstants.WOL_Config:
				return "wol";
			case AppConstants.HOTS_Config:
				return "hots";
			case AppConstants.LOTV_Config:
				return "lotv";
		}

		return "";
	}

	@Override
	public Context getContext() {
		return this.getActivity();
	}
}