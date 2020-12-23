package com.sc2toolslab.sc2bm.ui.presenters;

import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.EngineConsts;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessor;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;
import com.sc2toolslab.sc2bm.ui.model.AddItemDataItem;
import com.sc2toolslab.sc2bm.ui.providers.BuildProcessorConfigurationProvider;
import com.sc2toolslab.sc2bm.ui.utils.RequirementMessageFactory;
import com.sc2toolslab.sc2bm.ui.views.IBuildMakerAddItemView;

import java.util.ArrayList;
import java.util.List;

public class BuildMakerAddItemPresenter {
	private int mSelectedPosition;
	// private boolean mShowOnlyAvailableToProduce;
	private BuildItemTypeEnum mBuildItemType;
	private List<AddItemDataItem> mBuildItems;
	// private BuildItemStatistics mLastItemStats;
	// private BuildItemStatistics mCurrentStats;
	private BuildOrderProcessorData mBuildData;
	private BuildOrderProcessor mProcessor;

	private IBuildMakerAddItemView mView;

	public BuildMakerAddItemPresenter(IBuildMakerAddItemView view, BuildItemTypeEnum itemType, int selectedPosition) {
		this.mView = view;
		this.mBuildItemType = itemType;
		this.mSelectedPosition = selectedPosition;
		// this.mShowOnlyAvailableToProduce = showOnlyAvailableToProduce;

		this.mBuildItems = new ArrayList<>();

		this.mBuildData = BuildProcessorConfigurationProvider.getInstance().getLoadedBuildOrder();

		// this.mLastItemStats = mBuildData.getLastBuildItem().getStatisticsProvider();

		this.mProcessor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(mBuildData.generateBuildOrderEntity(), false);

		/*
		if (mSelectedPosition != -1) {
			mCurrentStats = mBuildData.getBuildItems().get(mSelectedPosition + 1).getStatisticsProvider();
		} else {
			mCurrentStats = mLastItemStats;
		}
	   */

		loadBuildItems();

		bindData();
	}

	public void setSelectedPosition(int selectedPosition) {
		mSelectedPosition = selectedPosition;

		loadBuildItems();

		bindData();
	}

	private void bindData() {
		mView.renderGrid(mBuildItems);
	}

	private void loadBuildItems() {
		List<AddItemDataItem> results = new ArrayList<>();

		BuildOrderProcessor tempProcessor = getProcessorForBuildUpToCurrentItem();
		BuildOrderProcessorItem selectedItem = tempProcessor.getCurrentBuildOrder().getLastBuildItem();

		BuildItemStatistics stats = selectedItem.getStatisticsProvider();

		for (BuildItemEntity item : BuildProcessorConfigurationProvider.getInstance().getLastItemsDictionary().clone().values()) {
			if (item.getItemType() != mBuildItemType || AppConstants.DEFAULT_STATE_ITEM_NAME.equals(item.getName())) {
				continue;
			}

			boolean isOrderSatisfied = true;

			for (IBuildItemRequirement requirement : item.getOrderRequirements()) {
				if (!requirement.isRequirementSatisfied(stats)) {
					isOrderSatisfied = false;
					break;
				}
			}

			boolean isProduceSatisfied = true;

			for (IBuildItemRequirement requirement : item.getProduceRequirements()) {
				if (!requirement.isRequirementSatisfied(stats)) {
					isProduceSatisfied = false;
					break;
				}
			}

			int needSeconds = 0;
			if (tempProcessor.addBuildItem(item.getName())) {
				BuildOrderProcessorItem tmpLastItem = tempProcessor.getCurrentBuildOrder().getLastBuildItem();

				needSeconds = tmpLastItem.getSecondInTimeLine() - selectedItem.getSecondInTimeLine();

				tempProcessor.undoLastBuildItem();
			}

			AddItemDataItem newItem = new AddItemDataItem();
			newItem.IsOrderAvailable = isOrderSatisfied;
			newItem.IsProduceAvailable = isProduceSatisfied;
			newItem.Item = item;
			newItem.NeededSeconds = needSeconds;
			newItem.Count = stats.getStatValueByName(item.getName());

			results.add(newItem);
		}

		mBuildItems = results;
	}

	private BuildOrderProcessor getProcessorForBuildUpToCurrentItem() {
		List<BuildOrderProcessorItem> items = mBuildData.getBuildOrderItemsClone();

		if (mSelectedPosition == items.size() - 2) {
			return mProcessor;
		}

		BuildOrderProcessorItem selectedItem = items.get(mSelectedPosition + 1);

		BuildOrderEntity newBuild = new BuildOrderEntity("",
				mBuildData.getsC2VersionID(),
				mBuildData.getDescription(),
				mBuildData.getRace(),
				mBuildData.getVsRace(),
				0,
				mBuildData.getCreated(),
				System.currentTimeMillis(),
				new ArrayList<String>());

		BuildOrderProcessor tempProcessor = new BuildOrderProcessor(mProcessor.getConfig());
		tempProcessor.loadBuildOrder(newBuild);

		for(BuildOrderProcessorItem item : items) {
			if (item.getItemName().equals(EngineConsts.DEFAULT_STATE_ITEM_NAME)) {
				continue;
			}

			tempProcessor.addBuildItem(item.getItemName());

			if (item == selectedItem) {
				break;
			}
		}

		return tempProcessor;
	}

	public BuildItemTypeEnum getItemType() {
		return mBuildItemType;
	}

	public void addItemRequested(int position) {
		AddItemDataItem buildItem = mBuildItems.get(position);

		if (AppConstants.IS_FREE && mBuildData.getLastBuildItem().getSecondInTimeLine() > 180) {
			mView.showMessage("You can't create builds longer than 3 minutes in FREE version of the tool.");
			return;
		}

		if (!buildItem.IsOrderAvailable) {
			mView.showItemInfoDialog(buildItem.Item);
		} else {
			mView.sendAddItemCommand(buildItem.Item.getName());
		}
	}

	public void newItemTypeRequested(BuildItemTypeEnum type) {
		mBuildItemType = type;
		loadBuildItems();
		bindData();
	}

	public void itemDetailsRequested(int position) {
		AddItemDataItem buildItem = mBuildItems.get(position);

		mView.showItemInfoDialog(buildItem.Item);
	}

	public RaceEnum getBuildFaction() {
		return mBuildData.getRace();
	}

	public String getRequirementMessage(BuildItemEntity item) {
		BuildOrderProcessorItem selectedItem = mBuildData.getBuildOrderItemsClone().get(mSelectedPosition + 1);

		String message = "";

		for (IBuildItemRequirement requirement : item.getOrderRequirements()) {
			String requirementMessage = RequirementMessageFactory.getRequirementMessage(requirement, BuildProcessorConfigurationProvider.getInstance().getLastItemsDictionary(), item);

			if (!"".equals(requirementMessage) && !requirement.isRequirementSatisfied(selectedItem.getStatisticsProvider())) {
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
