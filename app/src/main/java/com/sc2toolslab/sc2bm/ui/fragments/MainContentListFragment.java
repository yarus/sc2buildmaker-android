package com.sc2toolslab.sc2bm.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.adapters.BuildListGridViewAdapter;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.IFilterUpdated;
import com.sc2toolslab.sc2bm.ui.utils.NavigationManager;

import java.util.ArrayList;

public class MainContentListFragment extends ListFragment implements IFilterUpdated {
	private ListView lstBuilds;
	private BuildListGridViewAdapter mAdapter;
	private View mLayout;

	private RaceEnum mVsFaction;
	private ArrayList<BuildOrderEntity> mData;

	private void _loadAdapter() {
		mData = BuildOrdersProvider.getInstance(getActivity()).getBuildOrdersForFaction(mVsFaction);

		mAdapter = new BuildListGridViewAdapter(getActivity(), R.layout.fragment_main_content_list_item, mData);

		BuildOrdersProvider.getInstance(getActivity()).setOnFilterChangedListener(this);

		setListAdapter(mAdapter);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		lstBuilds = getListView();
		lstBuilds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
		mLayout = inflater.inflate(R.layout.fragment_main_content_list, container, false);

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
