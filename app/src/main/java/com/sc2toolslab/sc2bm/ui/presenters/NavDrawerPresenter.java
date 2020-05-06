package com.sc2toolslab.sc2bm.ui.presenters;

import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.IFilterUpdated;
import com.sc2toolslab.sc2bm.ui.views.INavDrawerView;

public class NavDrawerPresenter implements IPresenter, IFilterUpdated {
	private INavDrawerView mView;

	public NavDrawerPresenter(INavDrawerView view) {
		this.mView = view;

		BuildOrdersProvider.getInstance(mView.getContext()).setOnFilterChangedListener(this);

		_bindData();
	}

	@Override
	public void onFilterUpdated() {
		_bindData();
	}

	private void _bindData() {
		String version = BuildOrdersProvider.getInstance(mView.getContext()).getVersionFilter();
		RaceEnum mainFaction = BuildOrdersProvider.getInstance(mView.getContext()).getFactionFilter();

		mView.setFaction(mainFaction);
		mView.setVsTerranBuildCount(_getMatchCountVsFaction(mainFaction, RaceEnum.Terran), mainFaction);
		mView.setVsProtossBuildCount(_getMatchCountVsFaction(mainFaction, RaceEnum.Protoss), mainFaction);
		mView.setVsZergBuildCount(_getMatchCountVsFaction(mainFaction, RaceEnum.Zerg), mainFaction);
		mView.setAddon(version);
		mView.setVersion(version);
	}

	private int _getMatchCountVsFaction(RaceEnum mainFaction, RaceEnum vsFaction) {
		return BuildOrdersProvider.getInstance(mView.getContext()).getBuildOrdersCountForMatchup(mainFaction, vsFaction);
	}
}
