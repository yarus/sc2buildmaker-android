package com.sc2toolslab.sc2bm.ui.views;

import android.content.Context;

import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.ui.model.SimulationResultsData;

public interface ISimulatorResultsView {
    void render(SimulationResultsData data, BuildItemStatistics lastItemStats);
    void showMessage(String message);
    Context getContext();
}
