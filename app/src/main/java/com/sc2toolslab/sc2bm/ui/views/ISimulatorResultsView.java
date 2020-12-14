package com.sc2toolslab.sc2bm.ui.views;

import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;

import java.util.List;

public interface ISimulatorResultsView {
    void render(List<BuildItemEntity> buildItems, BuildItemStatistics currentStats);
    void showMessage(String message);
}
