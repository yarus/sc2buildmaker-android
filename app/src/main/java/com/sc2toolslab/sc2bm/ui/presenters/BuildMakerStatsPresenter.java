package com.sc2toolslab.sc2bm.ui.presenters;

import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.ui.views.IBuildMakerStatsView;

public class BuildMakerStatsPresenter implements IPresenter {
	private IBuildMakerStatsView mView;
	private BuildOrderProcessorData mBuildOrder;

	public BuildMakerStatsPresenter(IBuildMakerStatsView view, BuildOrderProcessorData buildOrder) {
		this.mView = view;
		this.mBuildOrder = buildOrder;

		mView.setLarvaVisibility(mBuildOrder.getRace() == RaceEnum.Zerg);
		mView.setSupplyImage(mBuildOrder.getRace());

		BuildOrderProcessorItem item = buildOrder.getLastBuildItem();
		bindData(item);
	}

	public void bindData(BuildOrderProcessorItem item) {
		mView.setTime(item.getSecondInTimeLine());

		BuildItemStatistics stats = item.getStatisticsProvider();

		mView.setEnergy(stats.getStatValueByName("TotalCasts"));
		mView.setLarva(stats.getStatValueByName("TotalLarva"));
		mView.setMinerals(stats.getMinerals());
		mView.setWorkersOnMinerals(stats.getWorkesOnMinerals());
		mView.setGas(stats.getGas());
		mView.setWorkersOnGas(stats.getWorkersOnGas());
		mView.setSupply(stats.getCurrentSupply(), stats.getMaximumSupply());
	}
}