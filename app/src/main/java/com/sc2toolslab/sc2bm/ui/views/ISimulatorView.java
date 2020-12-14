package com.sc2toolslab.sc2bm.ui.views;

import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;

import java.util.List;

public interface ISimulatorView extends IView {
    void showMessage(String message);
    void setPlayPauseImage(boolean isPlaying);
    void setKeepScreenFlag(boolean keepScreen);
    void renderBuildQueue(List<BuildOrderProcessorItem> buildItems, BuildOrderProcessorItem selectedItem);
    void renderProductionList(List<BuildItemEntity> buildItems, BuildItemStatistics currentStats, BuildItemEntity selectedItem);
    void renderProdOptionsList(List<BuildItemEntity> buildItems, BuildItemStatistics currentStats);
    void renderBuildStructureIcon(RaceEnum race, boolean isSelected);
}
