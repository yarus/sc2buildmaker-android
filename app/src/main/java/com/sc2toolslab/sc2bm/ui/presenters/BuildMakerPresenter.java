package com.sc2toolslab.sc2bm.ui.presenters;

import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessor;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.BuildProcessorConfigurationProvider;
import com.sc2toolslab.sc2bm.ui.views.IBuildMakerStatsView;
import com.sc2toolslab.sc2bm.ui.views.IBuildMakerView;

import java.util.ArrayList;
import java.util.List;

public class BuildMakerPresenter implements IPresenter {
	private IBuildMakerView mView;
	private BuildOrderProcessorData mBuildOrder;
	private BuildOrderProcessor mBuildProcessor;
	private BuildMakerStatsPresenter mStatsPresenter;
	private IBuildMakerStatsView mStatsView;

	private int mSelectedItemPosition = 0;

	public BuildMakerPresenter(IBuildMakerView view, IBuildMakerStatsView statsView, String buildName) {
		this.mView = view;
		this.mStatsView = statsView;

		BuildOrderEntity buildEntity;

		if (buildName.equals("")) {
			buildEntity = new BuildOrderEntity("",
					BuildOrdersProvider.getInstance(mView.getContext()).getVersionFilter(),
					"",
					BuildOrdersProvider.getInstance(mView.getContext()).getFactionFilter(),
					RaceEnum.NotDefined,
					0,
					System.currentTimeMillis(),
					System.currentTimeMillis(),
					new ArrayList<String>());
		} else {
			buildEntity = BuildOrdersProvider.getInstance(mView.getContext()).getBuildOrderByName(buildName);
		}

		mBuildProcessor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(buildEntity);
		mBuildOrder = mBuildProcessor.getCurrentBuildOrder();

		List<BuildOrderProcessorItem> items = mBuildOrder.getBuildOrderItemsClone();
		this.mSelectedItemPosition = items.size() - 2;

		bindData();

		mStatsPresenter = new BuildMakerStatsPresenter(statsView, this.mBuildOrder);
	}

	public void bindData() {
		mView.setBuildName(mBuildOrder.getName());

		List<BuildOrderProcessorItem> items = mBuildOrder.getBuildOrderItemsClone();

		mView.renderList(items.subList(1, items.size()));
	}

	public void setSelectedIndex(int index) {
		mSelectedItemPosition = index;

		List<BuildOrderProcessorItem> items = mBuildOrder.getBuildOrderItemsClone();

		BuildOrderProcessorItem selectedItem = items.get(mSelectedItemPosition + 1);
		mStatsPresenter.bindData(selectedItem);
		mView.renderList(items.subList(1, items.size()));
	}

	public void undoLastItem() {
		int size = mBuildOrder.getBuildItems().size();

		if (size == 1) {
			return;
		}

		mBuildProcessor.undoLastBuildItem();
		setSelectedIndex(mBuildOrder.getBuildOrderItemsClone().size() - 2);
	}

	public int getSelectedIndex() {
		return mSelectedItemPosition;
	}

	public String getBuildName() {
		return mBuildOrder.getName();
	}

	public void updateBuildName(String newBuildName) {
		BuildOrderEntity buildEntity = BuildOrdersProvider.getInstance(mView.getContext()).getBuildOrderByName(newBuildName);
		mBuildProcessor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(buildEntity);
		mBuildOrder = mBuildProcessor.getCurrentBuildOrder();

		List<BuildOrderProcessorItem> items = mBuildOrder.getBuildOrderItemsClone();
		this.mSelectedItemPosition = items.size() - 2;

		mStatsPresenter = new BuildMakerStatsPresenter(mStatsView, mBuildOrder);

		bindData();
	}

	public boolean addBuildItem(String itemName) {
		boolean result = mBuildProcessor.addBuildItem(itemName);
		if (result) {
			setSelectedIndex(mBuildOrder.getBuildOrderItemsClone().size() - 2);
		}
		return result;
	}

	public boolean isBuildModified() {
		if (mBuildOrder.getName().equals("")) {
			return mBuildOrder.getBuildItems().size() > 1;
		}

		BuildOrderEntity initialBuildOrder = BuildOrdersProvider.getInstance(mView.getContext()).getBuildOrderByName(mBuildOrder.getName());
		return !mBuildProcessor.getBuildEntityFormCurrentBuild().equals(initialBuildOrder);
	}

	public void unloadCurrentBuild() {
		mBuildProcessor.unloadBuildOrder();
	}
}
