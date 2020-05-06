package com.sc2toolslab.sc2bm.engine.modules;

import com.sc2toolslab.sc2bm.domain.ApplicationException;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;

import java.util.List;
import java.util.Map;

public class LotvChronoboostModule extends BuildManagerModuleBase {

	@Override
	public String getModuleName() {
		return "LotvChronoboostModule";
	}

	@Override
	public void adjustModuleStatsByUndoItem(BuildOrderProcessorItem undoBoItem, BuildOrderProcessorItem newLastItem) {
		if (undoBoItem.getItemName().toLowerCase().equals("chronoboost")) {
			if (undoBoItem.getSecondInTimeLine().equals(undoBoItem.getFinishedSecond())) {
				return;
			}

			int boostItemOrder = newLastItem.getStatisticsProvider().getStatValueByName("LastItemOrder");

			if (boostItemOrder == 0) {
				return;
			}

			BuildItemEntity boostItem = buildManagerConfiguration.getBuildItemsDictionary().getItem(newLastItem.getItemName());
			newLastItem.setFinishedSecond(newLastItem.getSecondInTimeLine() + boostItem.getBuildTimeInSeconds());
		}
	}

	@Override
	public void adjustModuleStatsByFinishedItems(List<BuildOrderProcessorItem> finishedItems, BuildItemStatistics stats) {
		for(BuildOrderProcessorItem item : finishedItems) {
			if (item.getItemName().toLowerCase().equals("nexus")) {
				stats.changeItemCountForName("BoostedNexusCount", 1);
				continue;
			}

			BuildItemEntity entity = buildManagerConfiguration.getBuildItemsDictionary().getItem(item.getItemName());
			if (entity.getProductionBuildingName() != null && entity.getProductionBuildingName().toLowerCase().equals("nexus")) {
				int initialBuildTime = entity.getBuildTimeInSeconds();
				int realBuildTime = item.getFinishedSecond() - item.getSecondInTimeLine();

				if (realBuildTime < initialBuildTime) {
					stats.changeItemCountForName("NexusBoostedBuzy", -1);
				}
			}
		}

		Map<String, Integer> chronoTimers = stats.getStatsWithKeyContains("ChronoTimer");
		for(Map.Entry<String, Integer> timer : chronoTimers.entrySet()) {
			if (timer.getValue() == 0) {
				int boostedNexusCount = stats.getStatValueByName("BoostedNexusCount");
				int nexusCount = stats.getStatValueByName("Nexus");

				if (nexusCount > boostedNexusCount) {
					stats.changeItemCountForName("BoostedNexusCount", 1);
				}

				stats.removeStat(timer.getKey());
			}
		}
	}

	@Override
	public void adjustModuleStatsForStep(BuildItemStatistics stats) {
		Map<String, Integer> chronoTimers = stats.getStatsWithKeyContains("ChronoTimer");
		for(Map.Entry<String, Integer> timer : chronoTimers.entrySet()) {
			if (timer.getValue() != 0) {
				stats.changeItemCountForName(timer.getKey(), -1);
			}
		}

		Map<String, Integer> chronoBuzyTimers = stats.getStatsWithKeyContains("ChronoBuzyTimer");
		for(Map.Entry<String, Integer> timer : chronoBuzyTimers.entrySet()) {
			if (timer.getValue() != 0) {
				stats.changeItemCountForName(timer.getKey(), -1);
			} else {
				stats.changeItemCountForName("NexusBoostedBuzy", 1);
				stats.removeStat(timer.getKey());
			}
		}
	}

	@Override
	public void adjustModuleStatsByStartedItem(BuildOrderProcessorItem boItem, BuildItemEntity item, BuildItemStatistics stats) throws ApplicationException {
		if (item.getName().toLowerCase().equals("defaultitem")) {
			stats.changeItemCountForName("BoostedNexusCount", 1);
			return;
		}

		if (item.getProductionBuildingName() != null && item.getProductionBuildingName().toLowerCase().equals("nexus")) {
			stats.setItemCountForName("ChronoboostAvailable", 0);

			int boostedNexus = stats.getStatValueByName("BoostedNexusCount");
			int boostedNexusBuzy = stats.getStatValueByName("NexusBoostedBuzy");

			if (boostedNexus > boostedNexusBuzy)
			{
				stats.changeItemCountForName("NexusBoostedBuzy", 1);
				int secondsToCompleteBuilding = boItem.getFinishedSecond() - boItem.getSecondInTimeLine();
				int secondsToCompleteWithBoost = (int) Math.round(secondsToCompleteBuilding - secondsToCompleteBuilding*0.15);

				boItem.setFinishedSecond(boItem.getSecondInTimeLine() + secondsToCompleteWithBoost);
			} else {
				Map<String, Integer> chronoTimers = stats.getStatsWithKeyContains("ChronoTimer");
				if (chronoTimers.size() > 0) {
					int fastestChronoRelease = getMinimumValue(chronoTimers);
					if (item.getBuildTimeInSeconds() > fastestChronoRelease)
					{
						int boostedTime = boItem.getFinishedSecond() - boItem.getSecondInTimeLine() - fastestChronoRelease;
						int boostedTimeWithBoost = (int)Math.round(boostedTime - boostedTime * 0.15);
						//var boostedTimeWithBoost = boostedTime / 1.5;

						boItem.setFinishedSecond(boItem.getSecondInTimeLine() + fastestChronoRelease + boostedTimeWithBoost);

						stats.setItemCountForName("ChronoBuzyTimer" + boItem.getOrder().toString(), fastestChronoRelease);
					}
				}
			}

			return;
		}

		if (item.getName().toLowerCase().equals("chronoboost"))
		{
			stats.setItemCountForName("ChronoboostAvailable", 0);

			int boostItemOrder = stats.getStatValueByName("LastItemOrder");

			BuildOrderProcessorItem boItemToBeBoosted = null;
			BuildItemEntity entityToBeBoosted = null;

			for (int i = boostItemOrder; i > 0; i--)
			{
				for(BuildOrderProcessorItem searchItem : buildOrder.getBuildOrderItemsClone()) {
					if(searchItem.getOrder() == i) {
						boItemToBeBoosted = searchItem;
						break;
					}
				}

				if (boItemToBeBoosted == null) {
					continue;
				}

				entityToBeBoosted = buildManagerConfiguration.getBuildItemsDictionary().getItem(boItemToBeBoosted.getItemName());

				if (boItemToBeBoosted.getFinishedSecond() < boItem.getSecondInTimeLine())
				{
					return;
				}

				if (entityToBeBoosted.getProductionBuildingName().toLowerCase().equals("nexus"))
				{
					continue;
				}

				if (entityToBeBoosted.getItemType() == BuildItemTypeEnum.Unit || entityToBeBoosted.getItemType() == BuildItemTypeEnum.Upgrade)
				{
					break;
				}

				boItemToBeBoosted = null;
				entityToBeBoosted = null;
			}

			if (boItemToBeBoosted == null || entityToBeBoosted == null)
			{
				throw new ApplicationException("Build processor were unable to find item to be boosted");
			}

			int unbooStedTime = 0;

			if (boItem.getSecondInTimeLine() > boItemToBeBoosted.getSecondInTimeLine())
			{
				unbooStedTime = boItem.getSecondInTimeLine() - boItemToBeBoosted.getSecondInTimeLine();
			}

			int boostedTime = boItemToBeBoosted.getFinishedSecond() - boItem.getSecondInTimeLine();

			int boostedTimeWithBoost = (int)Math.round(boostedTime - boostedTime * 0.15);

			boItemToBeBoosted.setFinishedSecond(boItemToBeBoosted.getSecondInTimeLine() + unbooStedTime + boostedTimeWithBoost);

			boItem.setFinishedSecond(boItemToBeBoosted.getFinishedSecond());

			stats.changeItemCountForName("BoostedNexusCount", -1);

			stats.setItemCountForName("ChronoTimer" + boostItemOrder, boItemToBeBoosted.getFinishedSecond() - boItem.getSecondInTimeLine());

			return;
		}

		if (item.getItemType() == BuildItemTypeEnum.Unit || item.getItemType() == BuildItemTypeEnum.Upgrade) {
			stats.setItemCountForName("LastItemOrder", boItem.getOrder());
			stats.setItemCountForName("ChronoboostAvailable", 1);
		}
	}

	private int getMinimumValue(Map<String, Integer> map) {
		int result = 999999999;

		for(Map.Entry<String, Integer> timer : map.entrySet()) {
			if (timer.getValue() < result) {
				result = timer.getValue();
			}
		}

		return result;
	}
}
