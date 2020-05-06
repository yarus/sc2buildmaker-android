package com.sc2toolslab.sc2bm.ui.views;

import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessor;

import java.util.List;

public interface IBuildMakerAddItemView {
	void renderGrid(BuildOrderProcessor processor, List<BuildItemEntity> data, BuildItemStatistics currentStats, BuildItemStatistics lastItemStats);
	void showItemInfoDialog(BuildItemEntity item);
	void sendAddItemCommand(String buildItemName);
	void showMessage(String message);
}