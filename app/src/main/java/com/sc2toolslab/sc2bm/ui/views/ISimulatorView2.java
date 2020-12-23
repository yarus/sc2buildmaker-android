package com.sc2toolslab.sc2bm.ui.views;

import android.content.Context;

import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.ui.model.QueueDataItem;
import com.sc2toolslab.sc2bm.ui.model.SimulatorDataItem;
import com.sc2toolslab.sc2bm.ui.presenters.SimulatorModeEnum;

import java.util.List;

public interface ISimulatorView2 {
    Context getContext();
    void showMessage(String message);
    void render(BuildOrderProcessorItem lastItem, List<QueueDataItem> mQueue, List<SimulatorDataItem> mMainList, List<SimulatorDataItem> buildItemsForStructure, BuildItemEntity buildStructure);
    void showItemsForStructure(List<SimulatorDataItem> items, BuildItemEntity buildStructure);
}
