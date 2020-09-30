package com.sc2toolslab.sc2bm.engine.modules;

import com.sc2toolslab.sc2bm.engine.EngineConsts;
import com.sc2toolslab.sc2bm.domain.ApplicationException;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;
import com.sc2toolslab.sc2bm.engine.requirements.CastRequirement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CastModule extends BuildManagerModuleBase {
	public static final String TOTAL_CASTS = "TotalCasts";
	public static final String CAST_TIMER = "CastTimer";
	public static final String CAST_COUNT = "CastCount";

	@Override
	public String getModuleName() {
		return "CastModule";
	}

	@Override
	public void adjustModuleStatsForStep(BuildItemStatistics stats) {
		Map<String, Integer> castTimers = stats.getStatsWithKeyContains(CAST_TIMER);
		Map<String, Integer> castCounts = stats.getStatsWithKeyContains(CAST_COUNT);

		if (castTimers.size() == 0) return;

		for(String key : castTimers.keySet()) {
			String nexusName = key.substring(0, key.length() - CAST_TIMER.length());
			int castCountStat = 0;
			String castKey = "";
			for(String castKeyTmp : castCounts.keySet()) {
				if(castKeyTmp.contains(nexusName)) {
					castCountStat = castCounts.get(castKeyTmp);
					castKey = castKeyTmp;
					break;
				}
			}
			int castTimer = castTimers.get(key);

			if (castCountStat < this.buildManagerConfiguration.getRaceConstants().getEnergyCastLimitPerEnergyGenerator()) {
				stats.changeItemCountForName(key, 1);
			}

			if (castTimer == this.buildManagerConfiguration.getRaceConstants().getEnergyCastGenerateTime()) {
				stats.changeItemCountForName(castKey, 1);
				stats.setItemCountForName(key, 0);
			}
		}

		this.resetTotalCast(stats);
	}

	@Override
	public void adjustModuleStatsByStartedItem(BuildOrderProcessorItem boItem, BuildItemEntity item, BuildItemStatistics stats) {
		if (item.getName().equals(EngineConsts.DEFAULT_STATE_ITEM_NAME)) {
			int energyGeneratorCount = stats.getStatValueByName(this.buildManagerConfiguration.getRaceConstants().getEnergyGeneratorBuildItemName());
			int energyForNewBase = this.buildManagerConfiguration.getRaceConstants().getEnergyCastCountForNewEnergyGenerator();
			String energyGeneratorName = this.buildManagerConfiguration.getRaceConstants().getEnergyGeneratorBuildItemName();

			stats.changeItemCountForName("TotalCasts", energyGeneratorCount * energyForNewBase);

			for (int i = 0; i < energyGeneratorCount; i++) {
				stats.changeItemCountForName(energyGeneratorName + (i + 1) + "CastCount", energyForNewBase);
				stats.changeItemCountForName(energyGeneratorName + (i + 1) + "CastTimer", 0);
			}
		}

		CastRequirement castRequirement = null;
		for(IBuildItemRequirement requirement : item.getProduceRequirements()) {
			if(requirement instanceof CastRequirement) {
				castRequirement = (CastRequirement) requirement;
				break;
			}
		}
		if (castRequirement == null) return;

		try {
			this.useCast(stats);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}

	public void useCast(BuildItemStatistics stats) throws ApplicationException {
		Map<String, Integer> castCounts = stats.getStatsWithKeyContains(CAST_COUNT);

		String castBuilding = "";
		int castCountValue = 0;

		for(String key : castCounts.keySet()) {
			if (castCounts.get(key) >= 1) {
				castBuilding = key;
				castCountValue = castCounts.get(key);
				break;
			}
		}

		if (castBuilding == null || "".equals(castBuilding)) {
			Map<String, Integer> castTimers = stats.getStatsWithKeyContains(CAST_TIMER);

			if (castTimers.size() == 0) {
				throw new ApplicationException("Energy generators are unavailable");
			}

			int maxTimer = Collections.max(castTimers.values());
			for(String key : castTimers.keySet()) {
				if(castTimers.get(key) == maxTimer) {
					castBuilding = key;
					break;
				}
			}
			castBuilding = castBuilding.substring(0, castBuilding.length() - CAST_TIMER.length());
			castCountValue = stats.getStatValueByName(castBuilding + CAST_COUNT);
			castBuilding = castBuilding + CAST_COUNT;
		}

		stats.setItemCountForName(castBuilding, castCountValue - 1);

		this.resetTotalCast(stats);
	}

	@Override
	public void adjustModuleStatsByFinishedItems(List<BuildOrderProcessorItem> finishedItems, BuildItemStatistics stats) {
		List<BuildOrderProcessorItem> finishedenergyGenerators = new ArrayList<BuildOrderProcessorItem>();
		for(BuildOrderProcessorItem item : finishedItems) {
			if(item.getItemName().equals(buildManagerConfiguration.getRaceConstants().getEnergyGeneratorBuildItemName())) {
				finishedenergyGenerators.add(item);
			}
		}

		if (finishedenergyGenerators.size() == 0) return;

		int energyGeneratorCount = stats.getStatValueByName(buildManagerConfiguration.getRaceConstants().getEnergyGeneratorBuildItemName());

		for(BuildOrderProcessorItem finishedGenerator : finishedenergyGenerators) {
			String energyGeneratorOrder = finishedGenerator.getItemName() + energyGeneratorCount++;
			stats.setItemCountForName(energyGeneratorOrder + CAST_COUNT, this.buildManagerConfiguration.getRaceConstants().getEnergyCastCountForNewEnergyGenerator());
			stats.setItemCountForName(energyGeneratorOrder + CAST_TIMER, 0);
		}
	}

	private void resetTotalCast(BuildItemStatistics stats) {
		Map<String, Integer> castCounts = stats.getStatsWithKeyContains(CAST_COUNT);
		if (castCounts.size() == 0) {
			stats.setItemCountForName(TOTAL_CASTS, 0);
		} else {
			int sum = 0;
			for(Integer value : castCounts.values()) {
				sum+=value;
			}
			stats.setItemCountForName(TOTAL_CASTS, sum);
		}
	}
}
