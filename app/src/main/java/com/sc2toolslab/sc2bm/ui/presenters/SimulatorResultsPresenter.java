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
import com.sc2toolslab.sc2bm.ui.model.SimulationResultsData;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.BuildProcessorConfigurationProvider;
import com.sc2toolslab.sc2bm.ui.views.ISimulatorResultsView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimulatorResultsPresenter {
    private final ISimulatorResultsView mView;
    private BuildItemStatistics mLastItemStatistics;
    private BuildOrderProcessorData mBuildOrder;
    private List<BuildOrderProcessorItem> mBuildItems;
    private final Collection<BuildItemEntity> mAllItems;

    private String mBuildName;

    public SimulatorResultsPresenter(ISimulatorResultsView view, String buildName) {
        this.mView = view;

        this.mBuildName = buildName;

        mBuildOrder = _getBuildOrder(buildName);

        mAllItems = BuildProcessorConfigurationProvider.getInstance().getLastItemsDictionary().clone().values();

        mBuildItems = mBuildOrder.getBuildItems();
        mLastItemStatistics = mBuildOrder.getLastBuildItem().getStatisticsProvider();

        SimulationResultsData stats = _loadStatistics();

        mView.render(stats, mLastItemStatistics);
    }

    private BuildOrderProcessorData _getBuildOrder(String buildName) {
        if (buildName.equals("") || buildName.equals("SYSTEM_SIMULATOR_RESULTS")) {
            return BuildProcessorConfigurationProvider.getInstance().getLoadedBuildOrder();
        } else {
            BuildOrderEntity bo = BuildOrdersProvider.getInstance(mView.getContext()).getBuildOrderByName(buildName);
            BuildOrderProcessor processor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(bo, true);
            return processor.getCurrentBuildOrder();
        }
    }

    public boolean isFreeSimulationMode() {
        return mBuildOrder.getName().equals("SYSTEM_SIMULATOR_RESULTS") || mBuildOrder.getName().equals("");
    }

    public String getBuildName() {
        return mBuildOrder.getName();
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

    private BuildItemEntity _getItemByName(String name) {
        BuildItemEntity entity = null;
        for (BuildItemEntity item : mAllItems) {
            if (item.getName().equals(name)) {
                entity = item;
            }
        }

        return entity;
    }

    private SimulationResultsData _loadStatistics() {
        int totalSpentMinerals = 0;
        int totalUnspentMinerals = 0;
        int totalSpentGas = 0;
        int totalUnspentGas = 0;
        int supplyCapTime = 0;
        int mainSaturationTiming = 0;
        int naturalSaturationTiming = 0;
        int thirdSaturationTiming = 0;

        boolean isSupplyCaped = false;
        int supplyCapStartSecond = 0;

        for (int i = 1;i < mBuildOrder.getBuildLengthInSeconds();i++) {
            List<BuildOrderProcessorItem> itemsForSecond = _getItemsForSecond(i);

            if (itemsForSecond.size() == 0) {
                continue;
            }

            int mineralSpentForSecond = 0;
            int gasSpentForSecond = 0;

            for (BuildOrderProcessorItem item : itemsForSecond) {
                if (item.getItemName().equals(AppConstants.DEFAULT_STATE_ITEM_NAME) || item.getItemType() == BuildItemTypeEnum.Special) {
                    continue;
                }

                BuildItemEntity itemInfo = _getItemByName(item.getItemName());

                mineralSpentForSecond += itemInfo.getCostMinerals();
                gasSpentForSecond += itemInfo.getCostGas();
            }

            BuildOrderProcessorItem lastItem = itemsForSecond.get(itemsForSecond.size() - 1);

            BuildItemStatistics stats = lastItem.getStatisticsProvider();

            totalSpentMinerals += mineralSpentForSecond;
            totalUnspentMinerals += stats.getMinerals();
            totalSpentGas += gasSpentForSecond;
            totalUnspentGas += stats.getGas();

            if (stats.getCurrentSupply().equals(stats.getMaximumSupply())) {
                isSupplyCaped = true;
                supplyCapStartSecond = lastItem.getSecondInTimeLine();
            } else if (isSupplyCaped) {
                supplyCapTime += lastItem.getSecondInTimeLine() - supplyCapStartSecond;
                isSupplyCaped = false;
                supplyCapStartSecond = 0;
            }

            int currentWorkersCount = _getWorkerCount(stats, mBuildOrder.getRace());

            if (mainSaturationTiming == 0 && currentWorkersCount >= 22) {
                mainSaturationTiming = lastItem.getSecondInTimeLine();
            }

            if (naturalSaturationTiming == 0 && currentWorkersCount >= 44) {
                naturalSaturationTiming = lastItem.getSecondInTimeLine();
            }

            if (thirdSaturationTiming == 0 && currentWorkersCount >= 66) {
                thirdSaturationTiming = lastItem.getSecondInTimeLine();
            }
        }

        int currentSupply = mLastItemStatistics.getCurrentSupply();
        int workersCount = _getWorkerCount(mLastItemStatistics, mBuildOrder.getRace());

        int lastUnspentMinerals = mLastItemStatistics.getMinerals();
        int lastUnspentGas = mLastItemStatistics.getGas();

        int totalUnspent = (totalUnspentMinerals + totalUnspentGas);

        int totalMinedMinerals = lastUnspentMinerals + totalSpentMinerals;
        int totalMinedGas = lastUnspentGas + totalSpentGas;
        int totalIncome = totalMinedMinerals + totalMinedGas;

        int buildLength = mBuildOrder.getBuildLengthInSeconds();
        if (buildLength == 0) {
            buildLength = 1;
        }

        double averageIncome = (totalIncome / buildLength);
        double averageUnspent = totalUnspent / buildLength;
        double mineralsToGasRatio = 0;

        if (totalMinedGas > 0) {
            mineralsToGasRatio = totalMinedMinerals / totalMinedGas;
        }

        // SQ(i,u)=35(0.00137i-ln(u))+240, where i=resource collection rate,
        // u=average unspent resources
        // sq = 35 * (0.00137 * avg_col_rate - math.log(avg_unspent)) + 240
        double averageSpendingQuotient = 35 * (0.00137 * averageIncome - Math.log(averageUnspent)) + 240;

        Collection<BuildItemEntity> allItems = BuildProcessorConfigurationProvider.getInstance().getLastItemsDictionary().clone().values();

        List<BuildItemEntity> Army = new ArrayList<>();
        List<BuildItemEntity> Buildings = new ArrayList<>();
        List<BuildItemEntity> Upgrades = new ArrayList<>();

        int armySupply = 0;

        for (BuildItemEntity item : allItems) {
            if (item.getItemType() == BuildItemTypeEnum.Special || AppConstants.DEFAULT_STATE_ITEM_NAME.equals(item.getName())) {
                continue;
            }

            int statValue = mLastItemStatistics.getStatValueByName(item.getName());
            if (statValue == 0) {
                continue;
            }

            if (item.getItemType() == BuildItemTypeEnum.Unit
                    && !(item.getName().equals("SCV") || item.getName().equals("Probe") || item.getName().equals("Drone") || item.getName().contains("WarpIn") || item.getName().contains("OnReactor"))) {
                armySupply += item.getCostSupply()*statValue;

                Army.add(item);
            } else if (item.getItemType() == BuildItemTypeEnum.Upgrade) {
                Upgrades.add(item);
            } else if (item.getItemType() == BuildItemTypeEnum.Building) {
                Buildings.add(item);
            }
        }

        SimulationResultsData results = new SimulationResultsData();

        results.Army = Army;
        results.Buildings = Buildings;
        results.Upgrades = Upgrades;
        results.TotalSpentMinerals = totalSpentMinerals;
        results.TotalSpentGas = totalSpentGas;
        results.TotalUnspentMinerals = totalUnspentMinerals;
        results.TotalUnspentGas = totalUnspentGas;
        results.TotalMinedMinerals = totalMinedMinerals;
        results.TotalMinedGas = totalMinedGas;
        results.MineralsToGasRatio = mineralsToGasRatio;
        results.TotalIncome = totalIncome;
        results.AverageUnspent = averageUnspent;
        results.AverageIncome = averageIncome*60;
        results.TotalUnspent = totalUnspent;
        results.SupplyCapTime = supplyCapTime;
        results.MainSaturationTiming = mainSaturationTiming;
        results.NaturalSaturationTiming = naturalSaturationTiming;
        results.ThirdSaturationTiming = thirdSaturationTiming;
        results.BuildLength = buildLength;
        results.Workers = workersCount;
        results.CurrentSupply = currentSupply;
        results.ArmySupply = armySupply;
        results.AverageSpendingQuotient = averageSpendingQuotient;

        results.Race = mBuildOrder.getRace();
        results.SC2Version = mBuildOrder.getsC2VersionID();
        results.BuildName = _getTitle();
        results.CreatedAt = mBuildOrder.getCreated();
        results.UpdatedAt = mBuildOrder.getVisited();

        return results;
    }

    private String _getTitle() {
        String header;
        if (isFreeSimulationMode()) {
            header = "Free Simulation";
        } else {
            header = getBuildName();
        }
        return header;
    }

    private int _getWorkerCount(BuildItemStatistics stats, RaceEnum race) {
        int workersCount;

        if (race == RaceEnum.Terran) {
            workersCount = stats.getStatValueByName("SCV");
        } else if (mBuildOrder.getRace() == RaceEnum.Protoss) {
            workersCount = stats.getStatValueByName("Probe");
        } else {
            workersCount = stats.getStatValueByName("Drone");
        }

        return workersCount;
    }

    private List<BuildOrderProcessorItem> _getItemsForSecond(int second) {
        List<BuildOrderProcessorItem> results = new ArrayList<>();

        for (BuildOrderProcessorItem item : mBuildItems) {
            if (item.getSecondInTimeLine() == second) {
                results.add(item);
            }

            if (item.getSecondInTimeLine() > second) {
                break;
            }
        }

        return results;
    }
}
