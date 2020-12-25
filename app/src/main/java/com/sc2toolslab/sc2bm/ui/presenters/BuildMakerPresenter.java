package com.sc2toolslab.sc2bm.ui.presenters;

import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.EngineConsts;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessor;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.ui.model.QueueDataItem;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.BuildProcessorConfigurationProvider;
import com.sc2toolslab.sc2bm.ui.views.IBuildMakerStatsView;
import com.sc2toolslab.sc2bm.ui.views.IBuildMakerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BuildMakerPresenter implements IPresenter {
	private final IBuildMakerView mView;
	private BuildOrderProcessorData mBuildOrder;
	private BuildOrderProcessor mBuildProcessor;
	private BuildMakerStatsPresenter mStatsPresenter;
	private final IBuildMakerStatsView mStatsView;
	private final Collection<BuildItemEntity> mAllItems;

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
		} else if (buildName.equals("SYSTEM_SIMULATOR_RESULTS")) {
			BuildOrderProcessorData loadedBuild = BuildProcessorConfigurationProvider.getInstance().getLoadedBuildOrder();
			buildEntity = loadedBuild.generateBuildOrderEntity();
		} else {
			buildEntity = BuildOrdersProvider.getInstance(mView.getContext()).getBuildOrderByName(buildName);
		}

		try {
			mBuildProcessor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(buildEntity, true);
		}
		catch(Exception e) {
			this.mView.showMessage(e.getMessage());
		}

		mBuildOrder = mBuildProcessor.getCurrentBuildOrder();

		mAllItems = BuildProcessorConfigurationProvider.getInstance().getLastItemsDictionary().clone().values();

		List<BuildOrderProcessorItem> items = mBuildOrder.getBuildOrderItemsClone();
		this.mSelectedItemPosition = items.size() - 2;

		mStatsPresenter = new BuildMakerStatsPresenter(statsView, this.mBuildOrder);

		bindData();
	}

	public void bindData() {
		mView.setBuildName(mBuildOrder.getName());

		List<BuildOrderProcessorItem> items = mBuildOrder.getBuildOrderItemsClone();

		BuildOrderProcessorItem selectedItem = items.get(mSelectedItemPosition + 1);

		mStatsPresenter.bindData(selectedItem);

		List<QueueDataItem> queue = _getQueue(items, items.get(mSelectedItemPosition + 1));

		mView.renderList(items.subList(1, items.size()), queue, items.get(mSelectedItemPosition + 1));
	}

	private List<QueueDataItem> _getQueue(List<BuildOrderProcessorItem> items, BuildOrderProcessorItem selectedItem) {
		List<BuildOrderProcessorItem> queue = new ArrayList<>();
		// Show only those items which already started but not yet finished
		for(BuildOrderProcessorItem item : items) {
			if (!item.getItemName().equals(EngineConsts.DEFAULT_STATE_ITEM_NAME) && item.getFinishedSecond() > selectedItem.getSecondInTimeLine() && item.getSecondInTimeLine() <= selectedItem.getSecondInTimeLine()) {
				queue.add(item);
			}
		}

		Collections.sort(queue, new Comparator<BuildOrderProcessorItem>() {
			public int compare(BuildOrderProcessorItem item1, BuildOrderProcessorItem item2) {
				return item1.getFinishedSecond().compareTo(item2.getFinishedSecond());
			}
		});

		List<QueueDataItem> filteredQueue = new ArrayList<>();
		QueueDataItem previousItem = null;
		for(BuildOrderProcessorItem item : queue) {
			if (previousItem != null && (previousItem.Item.getSecondInTimeLine().equals(item.getSecondInTimeLine())) && previousItem.Item.getItemName().equals(item.getItemName()) && previousItem.Item.getFinishedSecond().equals(item.getFinishedSecond())) {
				previousItem.Count++;
				continue;
			}

			QueueDataItem queueItem = new QueueDataItem();
			queueItem.Count = 1;
			queueItem.Item = item;

			if (mBuildOrder.getRace() == RaceEnum.Protoss && (item.getItemType() == BuildItemTypeEnum.Upgrade || item.getItemType() == BuildItemTypeEnum.Unit)) {
				BuildItemEntity itemEntity = _getItemByName(item.getItemName());
				queueItem.IsBoosted = (item.getFinishedSecond() - itemEntity.getBuildTimeInSeconds()) < item.getSecondInTimeLine();
			}

			filteredQueue.add(queueItem);
			previousItem = queueItem;
		}

		return filteredQueue;
	}

	private BuildItemEntity _getItemByName(String name) {
		BuildItemEntity entity = null;
		for (BuildItemEntity item : mAllItems) {
			if (item.getName().equals(name)) {
				entity = item;
			}
		}

		return entity;
	}

	public void setSelectedIndex(int index) {
		mSelectedItemPosition = index;

		bindData();
	}

	public boolean undoSelectedItem() {
		List<BuildOrderProcessorItem> items = mBuildOrder.getBuildOrderItemsClone();

		if (items.size() == 1) {
			return true;
		}

		BuildOrderProcessorItem selectedItem = items.get(mSelectedItemPosition + 1);

		BuildOrderEntity newBuild = new BuildOrderEntity(mBuildOrder.getName(),
				mBuildOrder.getsC2VersionID(),
				mBuildOrder.getDescription(),
				mBuildOrder.getRace(),
				mBuildOrder.getVsRace(),
				0,
				mBuildOrder.getCreated(),
				System.currentTimeMillis(),
				new ArrayList<String>());

		mBuildProcessor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(newBuild, true);

		boolean result = true;

		for(BuildOrderProcessorItem item : items) {
			if (item.getItemName().equals(EngineConsts.DEFAULT_STATE_ITEM_NAME) || item == selectedItem) {
				continue;
			}

			boolean addResult = mBuildProcessor.addBuildItem(item.getItemName());
			if (!addResult) {
				result = false;
			}
		}

		mBuildOrder = mBuildProcessor.getCurrentBuildOrder();

		mStatsPresenter = new BuildMakerStatsPresenter(mStatsView, this.mBuildOrder);

		if (mSelectedItemPosition > (mBuildOrder.getBuildItems().size() - 2)) {
			mSelectedItemPosition = mBuildOrder.getBuildItems().size() - 2;
		}

		bindData();

		return result;
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
		mBuildProcessor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(buildEntity, false);
		mBuildOrder = mBuildProcessor.getCurrentBuildOrder();

		List<BuildOrderProcessorItem> items = mBuildOrder.getBuildOrderItemsClone();
		this.mSelectedItemPosition = items.size() - 2;

		mStatsPresenter = new BuildMakerStatsPresenter(mStatsView, mBuildOrder);

		bindData();
	}

	public boolean addBuildItem(String itemName) {
		boolean result = true;

		if (mSelectedItemPosition == mBuildOrder.getBuildOrderItemsClone().size() - 2) {
			result = mBuildProcessor.addBuildItem(itemName);
			if (result) {
				setSelectedIndex(mBuildOrder.getBuildOrderItemsClone().size() - 2);
			}
		} else {
			// Add item in the middle
			List<BuildOrderProcessorItem> items = mBuildOrder.getBuildOrderItemsClone();

			BuildOrderProcessorItem selectedItem = items.get(mSelectedItemPosition + 1);

			BuildOrderEntity newBuild = new BuildOrderEntity(mBuildOrder.getName(),
					mBuildOrder.getsC2VersionID(),
					mBuildOrder.getDescription(),
					mBuildOrder.getRace(),
					mBuildOrder.getVsRace(),
					0,
					mBuildOrder.getCreated(),
					System.currentTimeMillis(),
					new ArrayList<String>());

			mBuildProcessor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(newBuild, true);

			result = true;

			for(BuildOrderProcessorItem item : items) {
				if (item.getItemName().equals(EngineConsts.DEFAULT_STATE_ITEM_NAME)) {
					continue;
				}

				mBuildProcessor.addBuildItem(item.getItemName());

				if (item == selectedItem) {
					boolean addNewResult = mBuildProcessor.addBuildItem(itemName);
					if (!addNewResult) {
						result = false;
						break;
					}
				}
			}

			mBuildOrder = mBuildProcessor.getCurrentBuildOrder();

			mStatsPresenter = new BuildMakerStatsPresenter(mStatsView, this.mBuildOrder);

			mSelectedItemPosition++;
		}

		bindData();

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
