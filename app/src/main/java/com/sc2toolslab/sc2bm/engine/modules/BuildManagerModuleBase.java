package com.sc2toolslab.sc2bm.engine.modules;

import com.sc2toolslab.sc2bm.domain.ApplicationException;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildOrderProcessorModule;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorConfiguration;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;

import java.util.List;

public abstract class BuildManagerModuleBase implements IBuildOrderProcessorModule {
	protected BuildOrderProcessorData buildOrder;
	protected BuildOrderProcessorConfiguration buildManagerConfiguration;

	public void adjustModuleStatsForStep(BuildItemStatistics stats) {
	}

	public void adjustModuleStatsByStartedItem(BuildOrderProcessorItem boItem, BuildItemEntity item, BuildItemStatistics stats) throws ApplicationException {
	}

	public void adjustModuleStatsByFinishedItems(List<BuildOrderProcessorItem> finishedItems, BuildItemStatistics stats) {
	}

	public void adjustModuleStatsByUndoItem(BuildOrderProcessorItem undoBoItem, BuildOrderProcessorItem newLastItem) {
	}

	public void initBuildOrder(BuildOrderProcessorData buildOrder, BuildOrderProcessorConfiguration config) {
		this.buildOrder = buildOrder;
		this.buildManagerConfiguration = config;
	}
}
