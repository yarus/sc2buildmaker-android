package com.sc2toolslab.sc2bm.engine.modules;

import com.sc2toolslab.sc2bm.domain.ApplicationException;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;
import com.sc2toolslab.sc2bm.engine.requirements.LarvaRequirement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LarvaModule extends BuildManagerModuleBase {
	@Override
	public String getModuleName() {
		return "LarvaModule";
	}

	public int getLarvaAdjuster() {
		return 4;
	}

	public int getSpawnLarvaTime() {
		return 15;
	}

	@Override
	public void adjustModuleStatsForStep(BuildItemStatistics stats) {
		Map<String, Integer> larvaTimers = stats.getStatsWithKeyContains("LarvaTimer");
		Map<String, Integer> larvaCounts = stats.getStatsWithKeyContains("LarvaCount");
		Map<String, Integer> injectedTimer = stats.getStatsWithKeyContains("InjectedTimer");

		if (larvaTimers.size() == 0 && injectedTimer.size() == 0) return;

		for (String key : larvaTimers.keySet()) {
			String hatcheryName = key.substring(0, key.length() - "LarvaTimer".length());
			int larvaCountStat = 0;
			String larvaCountStatKey = "";
			for(String statKey : larvaCounts.keySet()) {
				if(statKey.contains(hatcheryName)) {
					larvaCountStat = larvaCounts.get(statKey);
					larvaCountStatKey = statKey;
					break;
				}
			}

			if (larvaCountStat < 3) {
				stats.changeItemCountForName(key, 1);
			} else {
				stats.setItemCountForName(key, 0);
			}

			int newTime = stats.getStatValueByName(key);

			if (newTime == getSpawnLarvaTime()) {
				stats.changeItemCountForName(larvaCountStatKey, 1);
				stats.setItemCountForName(key, 0);
			}
		}

		for (String injectTimerKey : injectedTimer.keySet()) {
			String hatcheryName = injectTimerKey.substring(0, injectTimerKey.length() - "InjectedTimer".length());
			int larvaCount = stats.getStatValueByName(hatcheryName + "LarvaCount");

			stats.changeItemCountForName(hatcheryName + "InjectedTimer", 1);

			int newInjectTimerValue = stats.getStatValueByName(injectTimerKey);

			//int injectTime = this.BuildManagerConfiguration.BuildItemsDictionary.GetItem("InjectLarva").BuildTimeInSeconds;
			int injectTime = buildManagerConfiguration.getBuildItemsDictionary().getItem("InjectLarva").getBuildTimeInSeconds();

			if (newInjectTimerValue > injectTime) {
				int larvaChange = getLarvaAdjuster();
				if ((larvaCount + larvaChange) > 19) {
					larvaChange = 19 - larvaCount;
				}

				stats.changeItemCountForName(hatcheryName + "LarvaCount", larvaChange);
				stats.changeItemCountForName("TotalLarva", larvaChange);
				stats.removeStat(hatcheryName + "InjectedTimer");
			}
		}

		this.resetTotalLarva(stats);
	}

	@Override
	public void adjustModuleStatsByStartedItem(BuildOrderProcessorItem boItem, BuildItemEntity item, BuildItemStatistics stats) throws ApplicationException {
		if ("InjectLarva".equals(item.getName())) {
			// TODO:
			Map<String, Integer> injectedHatcheries = stats.getStatsWithKeyContains("InjectedTimer");
			int totalHatcheries = stats.getStatValueByName("Hatchery");

			if (injectedHatcheries.size() >= totalHatcheries) {
				throw new ApplicationException("All hatcheries are buzy");
			}

			List<String> injectedHatchList = new ArrayList<String>();
			for(String key : stats.getStatsWithKeyContains("InjectedTimer").keySet()) {
				injectedHatchList.add(key.substring(0, key.length() - "InjectedTimer".length()));
			}
			String hatchToInject = "";

			for (int i = 1; i <= totalHatcheries; i++) {
				String hatchName = "Hatchery" + i;
				if (!injectedHatchList.contains(hatchName)) {
					hatchToInject = hatchName;
					break;
				}
			}

			if ("".equals(hatchToInject)) {
				throw new ApplicationException("All hatcheries are buzy");
			}

			stats.setItemCountForName(hatchToInject + "InjectedTimer", 1);

			return;
		}

		LarvaRequirement larvaRequirement = null;
		for(IBuildItemRequirement requirement : item.getProduceRequirements()) {
			if(requirement instanceof LarvaRequirement) {
				larvaRequirement = (LarvaRequirement) requirement;
			}
		}
		if (larvaRequirement == null) return;

		int requiredLarva = larvaRequirement.getRequiredValue();

		Map<String, Integer> larvaCounts = stats.getStatsWithKeyContains("LarvaCount");
		for (String larvaCountKey : larvaCounts.keySet()) {
			if (larvaCounts.get(larvaCountKey) >= requiredLarva) {
				stats.setItemCountForName(larvaCountKey, larvaCounts.get(larvaCountKey) - requiredLarva);
				break;
			}

			requiredLarva = requiredLarva - larvaCounts.get(larvaCountKey);
			stats.setItemCountForName(larvaCountKey, 0);
		}

		this.resetTotalLarva(stats);
	}

	@Override
	public void adjustModuleStatsByFinishedItems(List<BuildOrderProcessorItem> finishedItems, BuildItemStatistics stats) {
		List<BuildOrderProcessorItem> finishedHatcheries = new ArrayList<BuildOrderProcessorItem>();
		for(BuildOrderProcessorItem item : finishedItems) {
			if("Hatchery".equals(item.getItemName())) {
				finishedHatcheries.add(item);
			}
		}
		int hatcheryCount = stats.getStatValueByName("Hatchery");

		for (BuildOrderProcessorItem finishedHatchery : finishedHatcheries) {
			String hatchOrder = finishedHatchery.getItemName() + String.valueOf(hatcheryCount++);
			stats.setItemCountForName(hatchOrder + "LarvaCount", 1);
			stats.setItemCountForName(hatchOrder + "LarvaTimer", 0);
		}
	}

	private void resetTotalLarva(BuildItemStatistics stats) {
		Map<String, Integer> larvaCounts = stats.getStatsWithKeyContains("LarvaCount");
		if (larvaCounts.size() == 0) {
			stats.setItemCountForName("TotalLarva", 0);
		} else {
			int sum = 0;
			for(Integer value : larvaCounts.values()) {
				sum+=value;
			}
			stats.setItemCountForName("TotalLarva", sum);
		}
	}
}
