package com.sc2toolslab.sc2bm.engine.domain;

import com.sc2toolslab.sc2bm.domain.ApplicationException;
import com.sc2toolslab.sc2bm.domain.ArgumentException;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.EngineConsts;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorConfiguration;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemAction;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildOrderProcessor {
	private BuildOrderProcessorData mBuildOrder;
	private BuildOrderProcessorConfiguration mConfig;

	public BuildOrderProcessor(BuildOrderProcessorConfiguration config) {
		this.mConfig = config;
	}

	public BuildOrderProcessorConfiguration getConfig() {
		return mConfig;
	}

	public BuildOrderEntity getBuildEntityFormCurrentBuild() {
		return this.mBuildOrder.generateBuildOrderEntity();
	}

	public void loadBuildDataFromEntity(BuildOrderEntity bo) {
		this.mBuildOrder.setName(bo.getName());
		this.mBuildOrder.setDescription(bo.getDescription());
		this.mBuildOrder.setRace(bo.getRace());
		this.mBuildOrder.setsC2VersionID(bo.getsC2VersionID());
		this.mBuildOrder.setVsRace(bo.getVsRace());
		this.mBuildOrder.setCreated(bo.getCreationDate());
		this.mBuildOrder.setVisited(bo.getVisitedDate());
	}

	public void unloadBuildOrder() {
		this.mBuildOrder = null;
	}

	public void loadBuildOrder(BuildOrderEntity bo) {
		this.mBuildOrder = new BuildOrderProcessorData();

		this.loadBuildDataFromEntity(bo);

		this.mConfig.getBuildManagerModules().initBuildManagerModules(this.mBuildOrder, this.mConfig);

		if (bo.getBuildOrderItems().size() == 0 || (bo.getBuildOrderItems().size() > 0 && !EngineConsts.DEFAULT_STATE_ITEM_NAME.equals(bo.getBuildOrderItems().get(0)))) {
			try {
				this.addDefaultBuildItemToNewBuildOrder(bo);
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
		}

		for (String item : bo.getBuildOrderItems()) {
			this.addBuildItem(item);
		}
	}

	public BuildOrderEntity createNewBuildOrder() {
		BuildOrderEntity bo = new BuildOrderEntity("", this.mConfig.getsC2VersionID(), "", this.mConfig.getRace(), RaceEnum.NotDefined, 0, System.currentTimeMillis(), System.currentTimeMillis(), new ArrayList<String>());

		this.loadBuildOrder(bo);

		return bo;
	}

	public boolean addBuildItem(String buildItemName) {
		int secondsToAppropriateItem = 0;
		BuildItemEntity buildItem = null;
		BuildItemStatistics statProvider = null;

		BuildItemEntity[] referenceToBuildItem = {buildItem};
		BuildItemStatistics[] referenceToStatProvider = {statProvider};

		try {
			getCurrentStateData(buildItemName, /*out*/ referenceToBuildItem, /*out*/ referenceToStatProvider);
		} catch (ArgumentException e) {
			e.printStackTrace();
			return false;
		}

		buildItem = referenceToBuildItem[0];
		statProvider = referenceToStatProvider[0];

		referenceToStatProvider[0] = statProvider;
		int[] referenceToSecondsToAppropriateItem = {secondsToAppropriateItem};
		try {
			findAppropriateSecondInTimeLine(buildItem, /*ref*/ referenceToStatProvider, /*out*/ referenceToSecondsToAppropriateItem);
		} catch (ArgumentException e) {
			e.printStackTrace();
			return false;
		}
		statProvider = referenceToStatProvider[0];
		secondsToAppropriateItem = referenceToSecondsToAppropriateItem[0];

		int secondInTimeLine = this.mBuildOrder.getLastBuildItem() != null
				? this.mBuildOrder.getLastBuildItem().getSecondInTimeLine()
				: 0;

		// Only Default Item should be added at second 0
		if (secondInTimeLine == 0 && !buildItem.getName().equals(EngineConsts.DEFAULT_STATE_ITEM_NAME)) {
			secondInTimeLine = 1;
		}

		BuildOrderProcessorItem buildOrderItem = createBuildOrderItemWithAdjustedResourcesAndStatistics(
				secondInTimeLine + secondsToAppropriateItem,
				buildItem,
				statProvider);

		this.mBuildOrder.addBuildItem(buildOrderItem);
		return true;
	}

	public void undoLastBuildItem() {
		if (EngineConsts.DEFAULT_STATE_ITEM_NAME.equals(mBuildOrder.getLastBuildItem().getItemName())) {
			return;
		}

		this.mConfig.getBuildManagerModules().adjustModulesStatsForUndo(this.mBuildOrder.getLastBuildItem(), this.mBuildOrder.getItemBefore(this.mBuildOrder.getLastBuildItem()));

		mBuildOrder.removeLastItem();
	}

	public void cancelBuildItem(BuildOrderProcessorItem item) {
		if (item == this.mBuildOrder.getLastBuildItem()) {
			undoLastBuildItem();
			return;
		}

		mBuildOrder.removeItem(item);

		BuildItemStatistics stats = mBuildOrder.getLastBuildItem().getStatisticsProvider();

		BuildItemEntity itemData = mConfig.getBuildItemsDictionary().getItem(item.getItemName());

		if (itemData.getCostSupply() > 0) {
			stats.setCurrentSupply(stats.getCurrentSupply() - itemData.getCostSupply());
		}

		stats.setGas(stats.getGas() + itemData.getCostGas());
		stats.setMinerals(stats.getMinerals() + itemData.getCostMinerals());
		stats.changeItemCountForName(itemData.getName() + EngineConsts.BUILD_ITEM_ON_BUILDING_POSTFIX, -1);
		if (itemData.getProductionBuildingName() != null) {
			stats.changeItemCountForName(itemData.getProductionBuildingName() + EngineConsts.BUZY_BUILD_ITEM_POSTFIX, -1);
		}

		if (itemData.getItemType() == BuildItemTypeEnum.Building && mConfig.getRace() == RaceEnum.Terran) {
			stats.setWorkesOnMinerals(stats.getWorkesOnMinerals() + 1);
		}
		else if (itemData.getItemType() == BuildItemTypeEnum.Building && mConfig.getRace() == RaceEnum.Zerg) {
			stats.setWorkesOnMinerals(stats.getWorkesOnMinerals() + 1);
			stats.setWorkersCount(stats.getWorkersCount() + 1);
		}

		this.mConfig.getBuildManagerModules().adjustModulesStatsForUndo(item, mBuildOrder.getLastBuildItem());
	}

	public BuildItemStatistics getCurrentStatistics() {
		return this.mBuildOrder.getLastBuildItem() != null ? this.mBuildOrder.getLastBuildItem().getStatisticsProvider() : null;
	}

	public BuildOrderProcessorData getCurrentBuildOrder() {
		return this.mBuildOrder;
	}

	private void getCurrentStateData(String buildItemName, /*out*/ BuildItemEntity[] buildItem, /*out*/ BuildItemStatistics[] statProvider) throws ArgumentException {
		buildItem[0] = this.mConfig.getBuildItemsDictionary().getItem(buildItemName);

		if (buildItem[0] == null) {
			throw new ArgumentException("Unknown build item");
		}

		Map<String, Integer> previousStats = this.mBuildOrder.getLastBuildItem() != null
				? this.mBuildOrder.getLastBuildItem().getStatisticsProvider().cloneItemsCountDictionary()
				: new HashMap<String, Integer>();

		statProvider[0] = new BuildItemStatistics(this.mConfig.getRaceConstants(), previousStats);
	}

	private void findAppropriateSecondInTimeLine(BuildItemEntity buildItem, /*ref*/ BuildItemStatistics[] stats, /*out*/ int[] secondsToAppropriateItem) throws ArgumentException {
		secondsToAppropriateItem[0] = 0;
		while ((!this.isCurrentSecondHasEnoughResourcesToBuildItem(buildItem, stats[0]) || !this.hasFreeProductionBuilding(stats[0], buildItem) || !this.isRequirementsSatisfied(buildItem, stats[0]))
				&& secondsToAppropriateItem[0] < this.mConfig.getGlobalConstants().getMaximumPeriodInSecondsForBuildPrediction()) {
			secondsToAppropriateItem[0]++;

			this.mConfig.getBuildManagerModules().adjustModulesStatsForStep(stats[0]);

			adjustStatisticsByFinishedItems(mBuildOrder.getLastBuildItem().getSecondInTimeLine() + secondsToAppropriateItem[0], stats[0]);

			checkForResourceChangePossibility(buildItem, stats[0]);

			if (secondsToAppropriateItem[0] == this.mConfig.getGlobalConstants().getMaximumPeriodInSecondsForBuildPrediction()) {
				throw new ArgumentException("There is no appropriate point in timeline when it is possible to build this item.");
			}
		}
	}

	private boolean isRequirementsSatisfied(BuildItemEntity buildItem, BuildItemStatistics stats) {
		if (buildItem.getProduceRequirements() == null || buildItem.getProduceRequirements().size() == 0) {
			return true;
		}

		for (IBuildItemRequirement requirement : buildItem.getProduceRequirements()) {
			if (!requirement.isRequirementSatisfied(stats)) {
				return false;
			}
		}

		return true;
	}

	private boolean hasFreeProductionBuilding(BuildItemStatistics currentStatProvider, BuildItemEntity buildItem) {
		if (buildItem.getProductionBuildingName() == null || "".equals(buildItem.getProductionBuildingName())) {
			return true;
		}

		int productionBuildingsCount = currentStatProvider.getStatValueByName(buildItem.getProductionBuildingName());

		int buzyProdCount = currentStatProvider.getStatValueByName(buildItem.getProductionBuildingName() + EngineConsts.BUZY_BUILD_ITEM_POSTFIX);

		return productionBuildingsCount > buzyProdCount;
	}

	private void addDefaultBuildItemToNewBuildOrder(BuildOrderEntity bo) throws ApplicationException {
		if (this.mBuildOrder.getLastBuildItem() == null) {
			BuildItemEntity item = this.mConfig.getBuildItemsDictionary().getItem(EngineConsts.DEFAULT_STATE_ITEM_NAME);
			if (item == null) {
				throw new ApplicationException("Default build item not specified");
			}

			this.mBuildOrder.addBuildItem(createBuildOrderItemWithAdjustedResourcesAndStatistics(1, item, new BuildItemStatistics(this.mConfig.getRaceConstants())));
			//this.addBuildItem(EngineConsts.DEFAULT_STATE_ITEM_NAME);
		}
	}

	private void checkForResourceChangePossibility(BuildItemEntity item, BuildItemStatistics statisticProvider) throws ArgumentException {
		if (item.getCostSupply() > 0 && (statisticProvider.getCurrentSupply() + item.getCostSupply() > statisticProvider.getMaximumSupply())) {
			if (statisticProvider.getStatValueByName(EngineConsts.CoreStatistics.BUILDING_NEW_SUPPLY) == 0) {
				throw new ArgumentException("New item requires more supply but there is no supply changers in build order");
			}
		}

		if (item.getCostMinerals() > 0) {
			if (statisticProvider.getMinerals() < item.getCostMinerals() && statisticProvider.getStatValueByName(EngineConsts.CoreStatistics.WORKERS_ON_MINERALS) == 0
					&& statisticProvider.getStatValueByName("MineralScv" + EngineConsts.BUILD_ITEM_ON_BUILDING_POSTFIX) == 0
					&& statisticProvider.getStatValueByName(EngineConsts.DEFAULT_STATE_ITEM_NAME + EngineConsts.BUILD_ITEM_ON_BUILDING_POSTFIX) == 0) {
				throw new ArgumentException("New item requires minerals but there is no mineral harvesters applied in build order");
			}
		}

		if (item.getCostGas() > 0) {
			if (statisticProvider.getGas() < item.getCostGas() && statisticProvider.getStatValueByName(EngineConsts.CoreStatistics.WORKERS_ON_GAS) == 0
					&& statisticProvider.getStatValueByName("GasScv" + EngineConsts.BUILD_ITEM_ON_BUILDING_POSTFIX) == 0
					&& statisticProvider.getStatValueByName(EngineConsts.DEFAULT_STATE_ITEM_NAME + EngineConsts.BUILD_ITEM_ON_BUILDING_POSTFIX) == 0) {
				throw new ArgumentException("New item requires gas but there is no gas harvesters applied in build order");
			}
		}
	}

	private BuildOrderProcessorItem createBuildOrderItemWithAdjustedResourcesAndStatistics(int secondInTimeLine, BuildItemEntity item, BuildItemStatistics stats) {
		stats.setCurrentSupply(stats.getCurrentSupply() + item.getCostSupply());

		BuildOrderProcessorItem newItem = new BuildOrderProcessorItem(secondInTimeLine, item, stats, this.getBuildItemOrder());

		this.runActions(item.getOrderedActions(), stats);

		this.mConfig.getBuildManagerModules().adjustModuleStatsByStartedItem(newItem, item, stats);

		return newItem;
	}

	private void runActions(List<IBuildItemAction> actions, BuildItemStatistics stats) {
		if (actions == null || actions.size() == 0) return;

		for (IBuildItemAction action : actions) {
			action.doAction(stats);
		}
	}

	private int getBuildItemOrder() {
		return mBuildOrder.getLastBuildItem() != null ? mBuildOrder.getLastBuildItem().getOrder() + 1 : 0;
	}

	private boolean isCurrentSecondHasEnoughResourcesToBuildItem(BuildItemEntity item, BuildItemStatistics stats) {
		if (stats == null) return false;

		return stats.getMinerals() >= item.getCostMinerals()
				&& stats.getGas() >= item.getCostGas()
				&& (item.getCostSupply() == 0 || (stats.getCurrentSupply() + item.getCostSupply() <= stats.getMaximumSupply()));
	}

	private void adjustStatisticsByFinishedItems(int currentSecond, BuildItemStatistics currentStatistics) {
		List<BuildOrderProcessorItem> finishedItems = this.mBuildOrder.getFinishedItemsClone(currentSecond);

		if (finishedItems.size() == 0) return;

		for (BuildOrderProcessorItem buildOrderItem : finishedItems) {
			BuildItemEntity buildItem = this.mConfig.getBuildItemsDictionary().getItem(buildOrderItem.getItemName());
			this.runActions(buildItem.getProducedActions(), currentStatistics);
		}

		this.mConfig.getBuildManagerModules().adjustModelStatsByFinishedItems(finishedItems, currentStatistics);
	}
}
