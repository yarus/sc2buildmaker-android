package com.sc2toolslab.sc2bm.engine.modules;

import com.sc2toolslab.sc2bm.domain.ApplicationException;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;
import com.sc2toolslab.sc2bm.engine.EngineConsts;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildOrderProcessorModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChronoboostModule extends BuildManagerModuleBase {
	private int secondsToChronoboost;

	@Override
	public String getModuleName() {
		return "ChronoboostModule";
	}

	@Override
	public void adjustModuleStatsForStep(BuildItemStatistics stats) {
		changeTimerForItems(stats.getStatsWithKeyContains("ChronoTimer"), stats);
		changeTimerForItems(stats.getStatsWithKeyContains("ChronoBuzy"), stats);
		changeTimerForItems(stats.getStatsWithKeyContains("NormalBuzy"), stats);
	}

	@Override
	public void adjustModuleStatsByStartedItem(BuildOrderProcessorItem boItem, BuildItemEntity item, BuildItemStatistics stats) {
		try {
			processBoostPossibleItem(boItem, item, stats);
			processChronoboostItem(boItem, item, stats);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		adjustAvailableBoost(boItem, item, stats);
	}

	@Override
	public void adjustModuleStatsByUndoItem(BuildOrderProcessorItem undoBoItem, BuildOrderProcessorItem newLastItem) {
		if ("Chronoboost".equals(undoBoItem.getItemName())) {
			// Recalculate boosted item finish time
			int boostItemOrder = newLastItem.getStatisticsProvider().getStatValueByName("LastItemOrder");
			int boostProdIndex = newLastItem.getStatisticsProvider().getStatValueByName("LastItemProdBuildingIndex");

			if (boostItemOrder == 0) {
				return;
			}

			BuildOrderProcessorItem boostBoItem = null;
			for(BuildOrderProcessorItem item : buildOrder.getBuildOrderItemsClone()) {
				if(item.getOrder() == boostItemOrder) {
					boostBoItem = item;
					break;
				}
			}
			BuildItemEntity boostItem = buildManagerConfiguration.getBuildItemsDictionary().getItem(boostBoItem.getItemName());

			int previousChronoTimer = newLastItem.getStatisticsProvider().getStatValueByName("ChronoTimer" + boostItem.getProductionBuildingName() + boostProdIndex);

			boostBoItem.setFinishedSecond(boostBoItem.getSecondInTimeLine() + this.calculateBuildTimeForItem(boostItem.getBuildTimeInSeconds(), previousChronoTimer));
		}
	}

	private void processBoostPossibleItem(BuildOrderProcessorItem boItem, BuildItemEntity item, BuildItemStatistics stats) throws ApplicationException {
		if (item.getItemType() == BuildItemTypeEnum.Unit || item.getItemType() == BuildItemTypeEnum.Upgrade) {
			String prodBuilding = this.getProdBuildingNameForItem(stats, item);

			if(!(prodBuilding == null || "".equals(prodBuilding))) {
				this.applyBuzyTimers(boItem, item, stats, prodBuilding);

				stats.setItemCountForName("LastItemOrder", boItem.getOrder());
				stats.setItemCountForName("LastItemProdBuildingIndex", Integer.valueOf(prodBuilding.substring(item.getProductionBuildingName().length())));
			}
		}
	}

	private void processChronoboostItem(BuildOrderProcessorItem boItem, BuildItemEntity item, BuildItemStatistics stats) throws ApplicationException {
		if (item.getItemType() == BuildItemTypeEnum.Special && "Chronoboost".equals(item.getName())) {
			if (stats.getStatValueByName("ChronoboostAvailable") == 0) {
				throw new ApplicationException("It is not possible to apply chronoboost at this moment!");
			}

			// Apply chronoboost calculations
			int boostItemOrder = stats.getStatValueByName("LastItemOrder");
			int boostProdIndex = stats.getStatValueByName("LastItemProdBuildingIndex");

			if (boostItemOrder == 0) {
				return;
			}

			BuildOrderProcessorItem boostBoItem = null;
			for(BuildOrderProcessorItem orderItem : buildOrder.getBuildOrderItemsClone()) {
				if(orderItem.getOrder() == boostItemOrder) {
					boostBoItem = orderItem;
					break;
				}
			}
			BuildItemEntity boostItem = this.buildManagerConfiguration.getBuildItemsDictionary().getItem(boostBoItem.getItemName());

			int previousChronoTimer = stats.getStatValueByName("ChronoTimer" + boostItem.getProductionBuildingName() + boostProdIndex);

			//int boostApplyPoint = boostBoItem.SecondInTimeLine + previousChronoTimer;

			int buildTime = boostBoItem.getFinishedSecond() - (boostBoItem.getSecondInTimeLine() + previousChronoTimer);

			boolean isEnoughEnergy = this.isChronoboostPossibleWhileBuilding(boostBoItem.getFinishedSecond() - boostBoItem.getSecondInTimeLine(), stats);
			if (!isEnoughEnergy) {
				throw new ApplicationException("Chronoboost is unavailable");
			}

			if (previousChronoTimer < secondsToChronoboost) {
				buildTime -= (secondsToChronoboost - previousChronoTimer);
			}

			int boostAmount = (buildTime - this.calculateBuildTimeForItem(buildTime, 20));
			boostBoItem.setFinishedSecond(boostBoItem.getFinishedSecond() - boostAmount);

			int normalBuzy = stats.getStatValueByName("NormalBuzy" + boostItem.getProductionBuildingName() + boostProdIndex);

			if (normalBuzy - (20 + boostAmount) > 0) {
				stats.setItemCountForName("NormalBuzy" + boostItem.getProductionBuildingName() + boostProdIndex, normalBuzy - (20 + boostAmount));
			} else {
				stats.removeStat("NormalBuzy" + boostItem.getProductionBuildingName() + boostProdIndex);
			}

			stats.setItemCountForName("ChronoTimer" + boostItem.getProductionBuildingName() + boostProdIndex, 20 + previousChronoTimer);

			int chronoBuzyTimer = (boostBoItem.getFinishedSecond() - boItem.getSecondInTimeLine()) > (20 + previousChronoTimer)
					? (20 + previousChronoTimer)
					: (boostBoItem.getFinishedSecond() - boItem.getSecondInTimeLine());
			stats.setItemCountForName("ChronoBuzy" + boostItem.getProductionBuildingName() + boostProdIndex, chronoBuzyTimer);

			CastModule castModule = null;
			for(IBuildOrderProcessorModule module : this.buildManagerConfiguration.getBuildManagerModules()) {
				if(module instanceof CastModule) {
					castModule = (CastModule)module;
				}
			}

			if (castModule != null) {
				castModule.useCast(stats);
			}
		}
	}

	private void adjustAvailableBoost(BuildOrderProcessorItem lastAddedBoItem, BuildItemEntity lastAddedItem, BuildItemStatistics stats) {
		if (lastAddedItem.getName().equals(EngineConsts.DEFAULT_STATE_ITEM_NAME)) return;

		BuildOrderProcessorItem boBoostedItem = null;
		BuildItemEntity boostedItem = null;

		if (lastAddedItem.getItemType() == BuildItemTypeEnum.Unit || lastAddedItem.getItemType() == BuildItemTypeEnum.Upgrade) {
			boBoostedItem = lastAddedBoItem;
			boostedItem = lastAddedItem;
		} else {
			int boostItemOrder = stats.getStatValueByName("LastItemOrder");
			for(BuildOrderProcessorItem orderItem : buildOrder.getBuildOrderItemsClone()) {
				if(orderItem.getOrder() == boostItemOrder) {
					boBoostedItem = orderItem;
					break;
				}
			}
			if(boBoostedItem != null) {
				boostedItem = this.buildManagerConfiguration.getBuildItemsDictionary().getItem(boBoostedItem.getItemName());
			}
		}

		if(boBoostedItem != null) {
			this.setAvailableBoost(boBoostedItem, boostedItem, stats);
		}
	}

	private void setAvailableBoost(BuildOrderProcessorItem boItem, BuildItemEntity item, BuildItemStatistics stats) {
		int boostProdIndex = stats.getStatValueByName("LastItemProdBuildingIndex");

		int chronoTimer = stats.getStatValueByName("ChronoTimer" + item.getProductionBuildingName() + boostProdIndex);
		int buildTime = boItem.getFinishedSecond() - boItem.getSecondInTimeLine();

		int availableCasts = buildTime <= chronoTimer ? 0 : (int)Math.ceil((double)(buildTime - chronoTimer) / 20);
		boolean isEnoughEnergy = this.isChronoboostPossibleWhileBuilding(buildTime, stats);

		if (boItem.getSecondInTimeLine() + secondsToChronoboost >= boItem.getFinishedSecond()) {
			isEnoughEnergy = false;
		}
		stats.setItemCountForName("ChronoboostAvailable", (availableCasts > 0 && isEnoughEnergy) ? 1 : 0);
	}

	private boolean isChronoboostPossibleWhileBuilding(int buildTime, BuildItemStatistics stats) {
		CastModule castModule = null;
		for(IBuildOrderProcessorModule module : this.buildManagerConfiguration.getBuildManagerModules()) {
			if(module instanceof CastModule) {
				castModule = (CastModule)module;
			}
		}

		BuildItemStatistics tempStats = new BuildItemStatistics(this.buildManagerConfiguration.getRaceConstants(), stats.cloneItemsCountDictionary());

		for (int i = 0; i < buildTime; i++) {

			if (castModule != null) {
				castModule.adjustModuleStatsForStep(tempStats);
			}

			if (tempStats.getStatValueByName(CastModule.TOTAL_CASTS) > 0) {
				secondsToChronoboost = i;
				return true;
			}
		}

		secondsToChronoboost = 0;
		return false;
	}

	private int calculateBuildTimeForItem(int unboostedTime, int boostTime) {
		double neededChronoTime = unboostedTime / 1.5;

		int resultBuildTime;

		if (neededChronoTime <= boostTime) {
			resultBuildTime = (int)Math.round(neededChronoTime);
		} else {
			double doneTime = unboostedTime * boostTime / neededChronoTime;
			resultBuildTime = (int)Math.floor(boostTime + (unboostedTime - doneTime));
		}

		return resultBuildTime;
	}

	private void applyBuzyTimers(BuildOrderProcessorItem boItem, BuildItemEntity item, BuildItemStatistics stats, String prodBuilding) {
		int chronoTimer = stats.getStatValueByName("ChronoTimer" + prodBuilding);
		if (chronoTimer != 0) {
			boItem.setFinishedSecond(boItem.getSecondInTimeLine() + this.calculateBuildTimeForItem(item.getBuildTimeInSeconds(), chronoTimer));

			int chronoBuzyTimer = (boItem.getFinishedSecond() - boItem.getSecondInTimeLine()) > chronoTimer
					? chronoTimer
					: (boItem.getFinishedSecond() - boItem.getSecondInTimeLine());

			stats.setItemCountForName("ChronoBuzy" + prodBuilding, chronoBuzyTimer);
		} else {
			stats.setItemCountForName("NormalBuzy" + prodBuilding, item.getBuildTimeInSeconds());
		}
	}

	private void changeTimerForItems(Map<String, Integer> list, BuildItemStatistics stats) {
		for (String key : list.keySet()) {
			stats.changeItemCountForName(key, -1);

			if (stats.getStatValueByName(key) == 0) {
				stats.removeStat(key);
			}
		}
	}

	private String getProdBuildingNameForItem(BuildItemStatistics stats, BuildItemEntity item) {
		String result = this.getFreeChronoboostedProdBuildingForItem(stats, item);

		if (result == null || "".equals(result)) {
			result = this.getFirstNormalProdBuilding(stats, item);
		}

		return result;
	}

	private String getFreeChronoboostedProdBuildingForItem(BuildItemStatistics stats, BuildItemEntity item) {
		String prodBuilding = "";

		Map<String, Integer> chronoedProdBuildings = stats.getStatsWithKeyContains("ChronoTimer" + item.getProductionBuildingName());
		Map<String, Integer>  buzyChronoedProdBuildings = stats.getStatsWithKeyContains("ChronoBuzy" + item.getProductionBuildingName());

		if (chronoedProdBuildings.size() > buzyChronoedProdBuildings.size()) {
			// Use free chronoed building with maximum chronoTimer
			int chronoTimer = 0;
			List<String> buzyProdNames = new ArrayList<>(buzyChronoedProdBuildings.keySet());
			for (String key : buzyChronoedProdBuildings.keySet())
			{
				if (!buzyProdNames.contains(key) && buzyChronoedProdBuildings.get(key) > chronoTimer) {
					chronoTimer = buzyChronoedProdBuildings.get(key);
					prodBuilding = key.substring("ChronoTimer".length());
				}
			}
		}

		return prodBuilding;
	}

	private String getFirstNormalProdBuilding(BuildItemStatistics stats, BuildItemEntity item) {
		int totalBuzyProdBuilding = stats.getStatValueByName(item.getProductionBuildingName() + EngineConsts.BUZY_BUILD_ITEM_POSTFIX);
		Map<String, Integer> buzyNormalProdBuildings = stats.getStatsWithKeyContains("NormalBuzy" + item.getProductionBuildingName());
		List<String> buzyProdNames = new ArrayList<>(buzyNormalProdBuildings.keySet());

		if (totalBuzyProdBuilding > buzyNormalProdBuildings.size()) {
			for (int i = 1; i <= totalBuzyProdBuilding; i++)
			{
				String tmpBuildingName = item.getProductionBuildingName() + i;
				if (!buzyProdNames.contains(tmpBuildingName)) {
					return tmpBuildingName;
				}
			}
		}

		return "";
	}
}
