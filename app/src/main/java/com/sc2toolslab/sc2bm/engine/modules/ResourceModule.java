package com.sc2toolslab.sc2bm.engine.modules;

import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.engine.EngineConsts;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.engine.domain.ResourcesEntity;

public class ResourceModule extends BuildManagerModuleBase {
	@Override
	public String getModuleName() {
		return "ResourceModule";
	}

	@Override
	public void adjustModuleStatsForStep(BuildItemStatistics stats) {
		ResourcesEntity resourceChange = this.getResourceChangeForPeriod(1, stats);
		stats.setMinerals(stats.getMinerals() + resourceChange.getMinerals());
		stats.setGas(stats.getGas() + resourceChange.getGas());
	}

	@Override
	public void adjustModuleStatsByStartedItem(BuildOrderProcessorItem boItem, BuildItemEntity item, BuildItemStatistics stats) {
		stats.setGas(stats.getGas() - item.getCostGas());
		stats.setMinerals(stats.getMinerals() - item.getCostMinerals());
	}

	private ResourcesEntity getResourceChangeForPeriod(int secondsInPeriod, BuildItemStatistics basePointStats) {
		int mineralsPerMinute = getMineralsPerMinute(basePointStats);
		int gasPerMinute = getGasPerMinute(basePointStats);

		ResourcesEntity incomeInfo = new ResourcesEntity(getAdjustedWithMilliResourcePerPeriod(mineralsPerMinute, secondsInPeriod, EngineConsts.CoreStatistics.MILLI_MINERALS, basePointStats),
				getAdjustedWithMilliResourcePerPeriod(gasPerMinute, secondsInPeriod, EngineConsts.CoreStatistics.MILLI_GAS, basePointStats));

		return incomeInfo;
	}

	private int getAdjustedWithMilliResourcePerPeriod(int resourcePerMinute, int secondsInPeriod,
	                                                  String milliResourceStatName,
	                                                  BuildItemStatistics basePointStats) {
		int resultResource = 0;

		if (resourcePerMinute < 60)
		{
			int milliValue = (resourcePerMinute * 100 / 60);
			basePointStats.changeItemCountForName(milliResourceStatName, milliValue);
			if (basePointStats.getStatValueByName(milliResourceStatName) > 100)
			{
				resultResource = 1;
				basePointStats.changeItemCountForName(milliResourceStatName, -100);
			}
		}
		else
		{
			resultResource = resourcePerMinute > 0 ? (resourcePerMinute / 60) * secondsInPeriod : 0;
			int milliValue = resourcePerMinute - resultResource*60;
			basePointStats.changeItemCountForName(milliResourceStatName, milliValue);
			if (basePointStats.getStatValueByName(milliResourceStatName) > 100)
			{
				resultResource++;
				basePointStats.changeItemCountForName(milliResourceStatName, -100);
			}
		}

		return resultResource;
	}

	private int getMineralsPerMinute(BuildItemStatistics basePointStats) {
		int result = calculateResourcePerMinute(
				basePointStats.getBasesCount(),
				this.buildManagerConfiguration.getGlobalConstants().getMineralsPatchesPerBase(),
				basePointStats.getWorkesOnMinerals(),
				this.buildManagerConfiguration.getGlobalConstants().getMineralsPerMinuteFrom3WorkersPerPatch(),
				this.buildManagerConfiguration.getGlobalConstants().getMineralsPerMinuteFrom2WorkersPerPatch(),
				this.buildManagerConfiguration.getGlobalConstants().getMineralsPerMinuteFrom1WorkerPerPatch());

		if (basePointStats.getMulesOnMinerals() > 0) {
			result += basePointStats.getMulesOnMinerals() * this.buildManagerConfiguration.getGlobalConstants().getMineralsPerMinuteFrom1Mule();
		}

		return result;
	}

	private int getGasPerMinute(BuildItemStatistics basePointStats) {
		return calculateResourcePerMinute(
				basePointStats.getBasesCount(),
				this.buildManagerConfiguration.getGlobalConstants().getGasPatchesPerBase(),
				basePointStats.getWorkersOnGas(),
				this.buildManagerConfiguration.getGlobalConstants().getGasPerMinuteFrom3WorkersPerPatch(),
				this.buildManagerConfiguration.getGlobalConstants().getGasPerMinuteFrom2WorkersPerPatch(),
				this.buildManagerConfiguration.getGlobalConstants().getGasPerMinuteFrom1WorkerPerPatch());
	}

	private int calculateResourcePerMinute(int basesCount, int patchesPerBase, int workersCountOnResource,
	                                       int incomeFrom3Workers, int incomeFrom2Workers, int incomeFrom1Worker) {
		int n = workersCountOnResource;
		int d = basesCount*patchesPerBase;
		int i3 = incomeFrom3Workers;
		int i2 = incomeFrom2Workers;
		int i1 = incomeFrom1Worker;

		int incomePerMinuteForPeriod = 0;

		if (n >= 3*d)
		{
			incomePerMinuteForPeriod = d*i3;
		}
		else if (n >= 2*d && n < 3*d)
		{
			int d3 = n - d*2;
			int d2 = d - d3;
			incomePerMinuteForPeriod = d3*i3 + d2*i2;
		}
		else if (n >= d && n < 2*d)
		{
			int d2 = n - d;
			int d1 = d - d2;
			incomePerMinuteForPeriod = d2*i2 + d1*i1;
		}
		else if (n <= d)
		{
			incomePerMinuteForPeriod = i1*n;
		}

		return incomePerMinuteForPeriod;
	}
}
