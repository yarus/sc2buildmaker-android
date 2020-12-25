package com.sc2toolslab.sc2bm.engine.modules;

import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;

public class IdleModule extends BuildManagerModuleBase {
	public static final String StartIdle = "StartIdleEnabled";
	public static final String IdleTimer = "IdleTimer";

	@Override
	public String getModuleName() {
		return "IdleModule";
	}

	@Override
	public void adjustModuleStatsByStartedItem(BuildOrderProcessorItem boItem, BuildItemEntity item, BuildItemStatistics stats) {
		int startIdle = stats.getStatValueByName(StartIdle);

		if (startIdle == 0)
		{
			stats.setItemCountForName(IdleTimer, 0);
		}
	}

	@Override
	public void adjustModuleStatsForStep(BuildItemStatistics stats) {
		int startIdle = stats.getStatValueByName(StartIdle);

		if (startIdle > 0) {
			stats.changeItemCountForName(IdleTimer, 1);
		} else {
			stats.setItemCountForName(IdleTimer, 0);
		}
	}
}