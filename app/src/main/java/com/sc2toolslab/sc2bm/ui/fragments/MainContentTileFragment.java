package com.sc2toolslab.sc2bm.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.adapters.BuildListGridViewAdapter;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.IFilterUpdated;
import com.sc2toolslab.sc2bm.ui.utils.NavigationManager;

import java.util.ArrayList;

public class MainContentTileFragment extends Fragment implements IFilterUpdated {
	private GridView gvBuilds;
	private int mItemSize, mItemSpacing;
	private BuildListGridViewAdapter mAdapter;
	private View mLayout;

	private RaceEnum mVsFaction;
	private ArrayList<BuildOrderEntity> mData;

	private void _loadAdapter() {
		gvBuilds = (GridView) mLayout.findViewById(R.id.gvBuilds);

		mItemSize = getResources().getDimensionPixelSize(R.dimen.tile_list_item_size);
		mItemSpacing = getResources().getDimensionPixelSize(R.dimen.tile_list_item_spacing);

		mData = BuildOrdersProvider.getInstance(getActivity()).getBuildOrdersForFaction(mVsFaction);

		mAdapter = new BuildListGridViewAdapter(getActivity(), R.layout.fragment_main_content_grid_item, mData);

		BuildOrdersProvider.getInstance(getActivity()).setOnFilterChangedListener(this);

		gvBuilds.setAdapter(mAdapter);
		gvBuilds.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (mAdapter.getNumColumns() == 0) {
					final int numColumns = (int) Math.floor(gvBuilds.getWidth() / (mItemSize + mItemSpacing));
					if (numColumns > 0) {
						final int columnWidth = (gvBuilds.getWidth() / numColumns) - mItemSpacing;
						mAdapter.setNumColumns(numColumns);
						mAdapter.setItemHeight(columnWidth);
					}
				}
			}
		});
		gvBuilds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BuildOrderEntity build = mData.get(position);

				NavigationManager.startBuildViewActivity(getActivity(), build.getName());
			}
		});
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mLayout = inflater.inflate(R.layout.fragment_main_content_grid, container, false);

		Bundle args = getArguments();
		if (args != null) {
			mVsFaction = RaceEnum.valueOf(args.getString("VsFaction"));

			_loadAdapter();
		}

		return mLayout;
	}

	@Override
	public void onFilterUpdated() {
		mData = BuildOrdersProvider.getInstance(getActivity()).getBuildOrdersForFaction(mVsFaction);

		mAdapter.updateData(mData);
	}
}
