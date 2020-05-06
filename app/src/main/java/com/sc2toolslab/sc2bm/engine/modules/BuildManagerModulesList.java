package com.sc2toolslab.sc2bm.engine.modules;

import com.sc2toolslab.sc2bm.domain.ApplicationException;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorConfiguration;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildOrderProcessorModule;

import java.util.ArrayList;
import java.util.List;

public class BuildManagerModulesList extends ArrayList<IBuildOrderProcessorModule> {

	public void initBuildManagerModules(BuildOrderProcessorData buildOrder, BuildOrderProcessorConfiguration config) {
		for(IBuildOrderProcessorModule module : this) {
			module.initBuildOrder(buildOrder, config);
		}
	}

	public void adjustModulesStatsForUndo(BuildOrderProcessorItem undoBoItem, BuildOrderProcessorItem newLastItem) {
		for(IBuildOrderProcessorModule stepModule : this) {
			stepModule.adjustModuleStatsByUndoItem(undoBoItem, newLastItem);
		}
	}

	public void adjustModulesStatsForStep(BuildItemStatistics stats) {
		for(IBuildOrderProcessorModule stepModule : this) {
			stepModule.adjustModuleStatsForStep(stats);
		}
	}

	public void adjustModuleStatsByStartedItem(BuildOrderProcessorItem boItem, BuildItemEntity item, BuildItemStatistics stats) {
		for(IBuildOrderProcessorModule buildStepModule : this) {
			try {
				buildStepModule.adjustModuleStatsByStartedItem(boItem, item, stats);
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
		}
	}

	public void adjustModelStatsByFinishedItems(List<BuildOrderProcessorItem> finishedItems, BuildItemStatistics stats) {
		for(IBuildOrderProcessorModule buildStepModule : this) {
			buildStepModule.adjustModuleStatsByFinishedItems(finishedItems, stats);
		}
	}

}