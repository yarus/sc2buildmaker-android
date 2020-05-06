package com.sc2toolslab.sc2bm.ui.presenters;

import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessor;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;
import com.sc2toolslab.sc2bm.ui.providers.BuildProcessorConfigurationProvider;
import com.sc2toolslab.sc2bm.ui.utils.RequirementMessageFactory;
import com.sc2toolslab.sc2bm.ui.views.IBuildMakerAddItemView;

import java.util.ArrayList;

public class BuildMakerAddItemPresenter {
	private int mSelectedPosition;
	private BuildItemTypeEnum mBuildItemType;
	private ArrayList<BuildItemEntity> mBuildItems;
	private BuildItemStatistics mLastItemStats;
	private BuildItemStatistics mCurrentStats;
	private BuildOrderProcessorData mBuildData;
	private BuildOrderProcessor mProcessor;

	private IBuildMakerAddItemView mView;

	public BuildMakerAddItemPresenter(IBuildMakerAddItemView view, BuildItemTypeEnum itemType, int selectedPosition) {
		this.mView = view;
		this.mBuildItemType = itemType;
		this.mSelectedPosition = selectedPosition;

		this.mBuildItems = new ArrayList<>();

		this.mBuildData = BuildProcessorConfigurationProvider.getInstance().getLoadedBuildOrder();

		this.mLastItemStats = mBuildData.getLastBuildItem().getStatisticsProvider();

		this.mProcessor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(mBuildData.generateBuildOrderEntity());

		if (mSelectedPosition != -1) {
			mCurrentStats = mBuildData.getBuildItems().get(mSelectedPosition + 1).getStatisticsProvider();
		} else {
			mCurrentStats = mLastItemStats;
		}

		loadBuildItems();

		mView.renderGrid(mProcessor, mBuildItems, mCurrentStats, mLastItemStats);
	}

	private void loadBuildItems() {
		mBuildItems.clear();
		for (BuildItemEntity item : BuildProcessorConfigurationProvider.getInstance().getLastItemsDictionary().clone().values()) {
			if (item.getItemType() == mBuildItemType && !AppConstants.DEFAULT_STATE_ITEM_NAME.equals(item.getName())) {
				mBuildItems.add(item);
			}
		}
	}

	public BuildItemTypeEnum getItemType() {
		return mBuildItemType;
	}

	public void addItemRequested(int position) {
		BuildItemEntity buildItem = mBuildItems.get(position);

		boolean satisfied = true;
		for (IBuildItemRequirement requirement : buildItem.getOrderRequirements()) {
			if (!requirement.isRequirementSatisfied(mLastItemStats)) {
				satisfied = false;
				break;
			}
		}

		if (AppConstants.IS_FREE && mBuildData.getLastBuildItem().getSecondInTimeLine() > 180) {
			mView.showMessage("You can't create builds longer than 3 minutes in FREE version of the tool.");
			return;
		}

		if (!satisfied) {
			mView.showItemInfoDialog(buildItem);
		} else {
			mView.sendAddItemCommand(buildItem.getName());
		}
	}

	public void newItemTypeRequested(BuildItemTypeEnum type) {
		mBuildItemType = type;
		loadBuildItems();
		mView.renderGrid(mProcessor, mBuildItems, mCurrentStats, mLastItemStats);
	}

	public void itemDetailsRequested(int position) {
		BuildItemEntity buildItem = mBuildItems.get(position);

		mView.showItemInfoDialog(buildItem);
	}

	public RaceEnum getBuildFaction() {
		return mBuildData.getRace();
	}

	public String getRequirementMessage(BuildItemEntity item) {
		String message = "";

		for (IBuildItemRequirement requirement : item.getOrderRequirements()) {
			String requirementMessage = RequirementMessageFactory.getRequirementMessage(requirement, BuildProcessorConfigurationProvider.getInstance().getLastItemsDictionary(), item);

			if (!"".equals(requirementMessage) && !requirement.isRequirementSatisfied(mLastItemStats)) {
				if (!"".equals(message)) {
					message += ", " + requirementMessage;
				} else {
					message = requirementMessage;
				}
			}
		}

		return message;
	}
}
