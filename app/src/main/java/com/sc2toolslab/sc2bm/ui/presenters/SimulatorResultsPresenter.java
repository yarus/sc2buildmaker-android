package com.sc2toolslab.sc2bm.ui.presenters;

import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.EngineConsts;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessor;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.BuildProcessorConfigurationProvider;
import com.sc2toolslab.sc2bm.ui.views.ISimulatorResultsView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimulatorResultsPresenter {
    private final ISimulatorResultsView mView;
    private BuildItemStatistics mLastItemStatistics;
    private List<BuildItemEntity> mStatisticsList;

    public SimulatorResultsPresenter(ISimulatorResultsView view) {
        this.mView = view;
    }

    public BuildItemStatistics getStatistics() {
        return mLastItemStatistics;
    }

    public void setStatistics(BuildItemStatistics lastItemStatistics) {
        mLastItemStatistics = lastItemStatistics;

        _loadStatisticsList();

        mView.render(mStatisticsList, mLastItemStatistics);
    }

    private void _loadStatisticsList() {
        List<BuildItemEntity> statisticsList = new ArrayList<>();

        Collection<BuildItemEntity> allItems = BuildProcessorConfigurationProvider.getInstance().getLastItemsDictionary().clone().values();

        for (BuildItemEntity item : allItems) {
            if (item.getItemType() != BuildItemTypeEnum.Special && !AppConstants.DEFAULT_STATE_ITEM_NAME.equals(item.getName())) {
                int statValue = mLastItemStatistics.getStatValueByName(item.getName());
                if (statValue > 0) {
                    statisticsList.add(item);
                }
            }
        }

        mStatisticsList = statisticsList;
    }

    public void cleanLoadedBuildOrder() {
        BuildOrderProcessorData loadedBuild = BuildProcessorConfigurationProvider.getInstance().getLoadedBuildOrder();

        BuildOrderEntity buildEntity = new BuildOrderEntity("",
                loadedBuild.getsC2VersionID(),
                "",
                loadedBuild.getRace(),
                RaceEnum.NotDefined,
                0,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                new ArrayList<String>());

        BuildOrderProcessor processor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(buildEntity, true);

        List<BuildOrderProcessorItem> buildItems = loadedBuild.getBuildItems();

        // mBuildOrder.getBuildItems()
        for (BuildOrderProcessorItem item : buildItems) {
            if (item.getItemName().equals("StartIdle") || item.getItemName().equals("StopIdleIn1Second") || item.getItemName().equals(EngineConsts.DEFAULT_STATE_ITEM_NAME)) {
                continue;
            }

            processor.addBuildItem(item.getItemName());
        }
    }
}
