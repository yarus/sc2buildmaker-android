package com.sc2toolslab.sc2bm.engine.interfaces;

import com.sc2toolslab.sc2bm.domain.ApplicationException;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorConfiguration;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;

import java.util.List;

public interface IBuildOrderProcessorModule {
	String getModuleName();

	void adjustModuleStatsForStep(BuildItemStatistics stats);

	void adjustModuleStatsByStartedItem(BuildOrderProcessorItem boItem, BuildItemEntity item, BuildItemStatistics stats) throws ApplicationException;

	void adjustModuleStatsByFinishedItems(List<BuildOrderProcessorItem> finishedItems, BuildItemStatistics stats);

	void adjustModuleStatsByUndoItem(BuildOrderProcessorItem undoBoItem, BuildOrderProcessorItem newLastItem);

	void initBuildOrder(BuildOrderProcessorData buildOrder, BuildOrderProcessorConfiguration settings);
}
