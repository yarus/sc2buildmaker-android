package com.sc2toolslab.sc2bm.ui.presenters;

import android.os.Handler;

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
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;
import com.sc2toolslab.sc2bm.ui.model.QueueDataItem;
import com.sc2toolslab.sc2bm.ui.model.SimulatorDataItem;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.BuildProcessorConfigurationProvider;
import com.sc2toolslab.sc2bm.ui.utils.UiDataViewHelper;
import com.sc2toolslab.sc2bm.ui.views.IBuildMakerStatsView;
import com.sc2toolslab.sc2bm.ui.views.ISimulatorView2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

public class SimulatorPresenter2 {
    private final ISimulatorView2 mView;
    private final String mGameSpeed;
    private int mCurrentSecond;
    private boolean mIsPlaying;
    private SimulatorModeEnum mCurrentMode; // BASE | BUILD | ARMY

    private final BuildMakerStatsPresenter mStatsPresenter;
    private BuildOrderProcessorData mBuildOrder;
    private BuildOrderProcessor mBuildProcessor;

    private List<QueueDataItem> mQueue;
    private List<SimulatorDataItem> mMainList;
    private List<SimulatorDataItem> mBuildItemsForStructure;
    private BuildItemEntity mBuildStructure;
    private final Collection<BuildItemEntity> mAllItems;

    private final Handler mHandler;
    private final Runnable mTimerTick;

    public SimulatorPresenter2(ISimulatorView2 view, IBuildMakerStatsView statsView, String gameSpeed) {
        this.mView = view;
        this.mGameSpeed = gameSpeed;
        this.mCurrentSecond = 0;
        this.mIsPlaying = false;

        this.mCurrentMode = SimulatorModeEnum.BASE;

        _initBuildOrder();

        mAllItems = BuildProcessorConfigurationProvider.getInstance().getLastItemsDictionary().clone().values();

        mStatsPresenter = new BuildMakerStatsPresenter(statsView, this.mBuildOrder);

        this.mHandler = new Handler();
        this.mTimerTick = _loadTimerHandler();

        _reloadData();

        _bindData();
    }

    public void changeMode(SimulatorModeEnum mode) {
        mCurrentMode = mode;

        _reloadData();

        _bindData();
    }

    public void setCurrentSecond(int currentSecond) {
        this.mCurrentSecond = currentSecond;

        _bindData();
    }

    public void cancelBuildItem(BuildOrderProcessorItem item) {
        mBuildProcessor.cancelBuildItem(item);

        _reloadData();

        _bindData();
    }

    public boolean getIsPlaying() {
        return mIsPlaying;
    }

    public RaceEnum getRace() { return mBuildOrder.getRace(); }

    public void changePlayMode() {
        mIsPlaying = !mIsPlaying;

        if(mIsPlaying) {
            _runTimer();
        } else {
            _pausePlaying();
        }

        _bindData();
    }

    public void finish() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mTimerTick);
        }
    }

    public void reset() {
        boolean needToStart = false;

        if (mIsPlaying) {
            needToStart = true;
            _pausePlaying();
        }

        mCurrentSecond -= 5;
        if (mCurrentSecond < 0) {
            mCurrentSecond = 0;
        }

        BuildOrderProcessorItem item = mBuildOrder.getLastBuildItem();

        while(item.getSecondInTimeLine() >= mCurrentSecond && !item.getItemName().equals(EngineConsts.DEFAULT_STATE_ITEM_NAME)) {
            mBuildProcessor.undoLastBuildItem();

            item = mBuildOrder.getLastBuildItem();
        }

        if (item.getItemName().equals("StartIdle")) {
            mBuildProcessor.addBuildItem("StopIdleIn1Second");
        }

        _reloadData();

        _bindData();

        if (needToStart) {
            mIsPlaying = true;
            _runTimer();
        }
    }

    public BuildItemStatistics getLastItemStatistics() {
        return mBuildProcessor != null ? mBuildProcessor.getCurrentStatistics() : null;
    }

    public int getCurrentSecond() {
        return mCurrentSecond;
    }

    public SimulatorModeEnum getCurrentMode() {
        return mCurrentMode;
    }

    public boolean addBuildItem(String itemName) {
        if (mCurrentSecond == 0) {
            return false;
        }

        boolean result = mBuildProcessor.addBuildItem(itemName);

        _reloadData();

        _bindData();

        return result;
    }

    public void setBuildStructure(String name) {
        if (name == null || name.equals("")) {
            mBuildStructure = null;
            mBuildItemsForStructure = new ArrayList<>();
            return;
        }

        mBuildStructure = _getItemByName(name);

        _loadItemsForBuildStructure(mBuildStructure.getName());

        mView.showItemsForStructure(mBuildItemsForStructure, mBuildStructure);
    }

    private void _bindData() {
        mStatsPresenter.bindStats(mCurrentSecond, mBuildProcessor.getCurrentStatistics());

        mView.render(mBuildOrder.getLastBuildItem(), mQueue, mMainList, mBuildItemsForStructure, mBuildStructure);
    }

    private Runnable _loadTimerHandler() {
        return new Runnable() {
            @Override
            public void run() {
                mBuildProcessor.addBuildItem("StartIdle");
                mBuildProcessor.addBuildItem("StopIdleIn1Second");

                mBuildOrder = mBuildProcessor.getCurrentBuildOrder();

                if (AppConstants.IS_FREE && mCurrentSecond >= 180) {
                    mView.showMessage("You can't play builds longer than 3 minutes in FREE version of the tool.");
                    _pausePlaying();
                    return;
                }

                mCurrentSecond++;

                _reloadData();

                _bindData();

                _runTimer();
            }
        };
    }

    private void _pausePlaying() {
        mIsPlaying = false;

        mHandler.removeCallbacks(mTimerTick);
    }

    private void _reloadData() {
        _loadQueue();
        _loadMainList();

        if (mBuildStructure != null) {
            _loadItemsForBuildStructure(mBuildStructure.getName());
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

    private boolean _isProductionItem(String name) {
        for (BuildItemEntity item : mAllItems) {
            String prodName = item.getProductionBuildingName();
            if (prodName != null && prodName.equals(name)) {
                return true;
            }
        }

        return false;
    }

    private void _loadItemsForBuildStructure(String name) {
        List<SimulatorDataItem> results = new ArrayList<>();
        List<SimulatorDataItem> secondaryResults = new ArrayList<>();

        if (name == null) {
            mBuildItemsForStructure = null;
            mQueue = new ArrayList<>();
            return;
        }

        for (BuildItemEntity item : mAllItems) {
            if ((name.equals("Refinery") && (item.getName().equals("GasScv") && mBuildOrder.getRace() == RaceEnum.Terran))
                    || (name.equals("Assimilator") && (item.getName().equals("GasProbe") && mBuildOrder.getRace() == RaceEnum.Protoss))
                    || (name.equals("Extractor") && (item.getName().equals("GasDrone") && mBuildOrder.getRace() == RaceEnum.Zerg))) {
                boolean hasFreeSpace = _hasFreeSpaceOnGas(mBuildOrder.getLastBuildItem().getStatisticsProvider());
                if (hasFreeSpace) {
                    SimulatorDataItem dataItem = _generateDataItem(item, true, true, 1);
                    results.add(dataItem);
                    continue;
                }
            }

            if (((name.equals("CommandCenter") || name.equals("OrbitalCommand") || name.equals("PlanetaryFortrees")) && (item.getName().equals("CallMule") || item.getName().equals("CallSupplyDrop") || item.getName().equals("ScannerSweep")))
                    || (name.equals("Queen") && (item.getName().equals("InjectLarva") || item.getName().equals("SpawnCreepTumor")))) {
                // Check if Mule is avaialable and CommandCenter is not yet added
                int castsCount = mBuildOrder.getLastBuildItem().getStatisticsProvider().getStatValueByName("TotalCasts");
                SimulatorDataItem dataItem = _generateDataItem(item, castsCount > 0, castsCount > 0, 1);
                secondaryResults.add(dataItem);
            }

            if (item.getProductionBuildingName() != null && item.getProductionBuildingName().equals("CommandCenter") && (name.equals("OrbitalCommand") || name.equals("PlanetaryFortrees"))) {
                boolean satisfied = _isRequirementsSatisfied(item);
                SimulatorDataItem dataItem = _generateDataItem(item, satisfied, satisfied, 1);
                results.add(dataItem);
                continue;
            }

            if (item.getProductionBuildingName() != null
                    && ((name.equals("Hatchery") && !item.getName().equals("Hive")) || (name.equals("Lair") && !item.getName().equals("Lair")) || (name.equals("Hive") && !item.getName().equals("Hive")))
                    && ((item.getProductionBuildingName().equals("FreeHatcheryForUpgrades"))
                        || (item.getProductionBuildingName().equals("Hatchery"))
                        || (item.getProductionBuildingName().equals("FreeHatcheryForLair"))
                        || (item.getProductionBuildingName().equals("FreeLairForHive"))
                        || (item.getProductionBuildingName().equals("FreeHatcheryForQueen")))) {
                boolean satisfied = _isRequirementsSatisfied(item);
                SimulatorDataItem dataItem = _generateDataItem(item, satisfied, satisfied, 1);
                results.add(dataItem);
                continue;
            }

            if (name.equals("Gateway") && ((item.getProductionBuildingName() != null && item.getProductionBuildingName().equals("FreeGatewayForUnit")) || item.getName().equals("SwitchToWarpgate"))) {
                boolean satisfied = _isRequirementsSatisfied(item);
                SimulatorDataItem dataItem = _generateDataItem(item, satisfied, satisfied, 1);
                if (item.getName().equals("SwitchToWarpgate")) {
                    secondaryResults.add(dataItem);
                } else {
                    results.add(dataItem);
                }

                continue;
            }

            if (name.equals("SwitchToWarpgate") && ((item.getProductionBuildingName() != null && item.getProductionBuildingName().equals("FreeWarpgateForUnit")))) {
                boolean satisfied = _isRequirementsSatisfied(item);
                SimulatorDataItem dataItem = _generateDataItem(item, satisfied, satisfied, 1);
                results.add(dataItem);
                continue;
            }

            // Skip level 2 and level 3 upgrades here since they will be calculated in the code abovc
            if (item.getName().contains("Level")) {
                int levelNumber = (int)item.getName().charAt(item.getName().length() - 1);
                int prevLevel = levelNumber - 1;

                if (prevLevel == 0) {
                    boolean satisfied = _isRequirementsSatisfied(item);
                    SimulatorDataItem dataItem = _generateDataItem(item, satisfied, satisfied, 1);
                    results.add(dataItem);
                } else {
                    String baseItemName = item.getName().substring(0, item.getName().length() - ("Level".length() + 1));
                    String pervLevelItemName = baseItemName + prevLevel;

                    int done = mBuildOrder.getLastBuildItem().getStatisticsProvider().getStatValueByName(pervLevelItemName);
                    if (done > 0) {
                        boolean satisfied = _isRequirementsSatisfied(item);
                        SimulatorDataItem dataItem = _generateDataItem(item, satisfied, satisfied, 1);
                        results.add(dataItem);
                    }
                }
            }

            if (item.getName().contains("OnReactor")) {
                String itemName = item.getName();
                String baseItemName = itemName.substring(0, itemName.length() - "OnReactor".length());

                BuildItemEntity baseItem = _getItemByName(baseItemName);

                if (baseItem != null && baseItem.getProductionBuildingName() != null && baseItem.getProductionBuildingName().equals(name)) {
                    boolean satisfied = _isRequirementsSatisfied(item);
                    SimulatorDataItem dataItem = _generateDataItem(item, satisfied, satisfied, 1);
                    results.add(dataItem);
                }

                continue;
            }

            if (item.getProductionBuildingName() == null || item.getItemType() == BuildItemTypeEnum.Special || !item.getProductionBuildingName().equals(name)) {
                continue;
            }

            boolean isEnabled = _isRequirementsSatisfied(item);
            boolean isClickable = isEnabled;

            if (item.getItemType() == BuildItemTypeEnum.Upgrade && !isEnabled) {
                int done = mBuildOrder.getLastBuildItem().getStatisticsProvider().getStatValueByName(item.getName());

                if (done > 0) {
                    isEnabled = true;
                    isClickable = false;
                }
            }

            SimulatorDataItem dataItem = _generateDataItem(item, isEnabled, isClickable, 1);
            results.add(dataItem);
        }

        results.addAll(secondaryResults);

        mBuildItemsForStructure = results;
    }

    private void _loadQueue() {
        List<BuildOrderProcessorItem> queue = new ArrayList<>();
        List<BuildOrderProcessorItem> items = mBuildOrder.getBuildOrderItemsClone();

        // Show only those items which already started but not yet finished
        for(BuildOrderProcessorItem item : items) {
            BuildItemTypeEnum type = item.getItemType();
            int finishedSecond = item.getFinishedSecond();
            int secondInTimeLine = item.getSecondInTimeLine();
            if (!item.getItemName().equals(EngineConsts.DEFAULT_STATE_ITEM_NAME) && finishedSecond > mCurrentSecond && secondInTimeLine <= mCurrentSecond) {
                queue.add(item);
            }
        }

        List<QueueDataItem> filteredQueue = new ArrayList<>();
        QueueDataItem previousItem = null;
        for(BuildOrderProcessorItem item : queue) {
            if (previousItem != null && (previousItem.Item.getSecondInTimeLine().equals(item.getSecondInTimeLine())) && previousItem.Item.getItemName().equals(item.getItemName())) {
                previousItem.Count++;
                continue;
            }

            QueueDataItem queueItem = new QueueDataItem();
            queueItem.Count = 1;
            queueItem.Item = item;
            filteredQueue.add(queueItem);
            previousItem = queueItem;
        }

        mQueue = filteredQueue;
    }

    private void _loadMainList() {
        List<SimulatorDataItem> mainList = new ArrayList<>();

        if (mCurrentMode == SimulatorModeEnum.BASE) {
            mainList = _getBaseItems();
        } else if (mCurrentMode == SimulatorModeEnum.BUILD) {
            mainList = _getBuildItems();
        } else if (mCurrentMode == SimulatorModeEnum.SPECIAL) {
            mainList = _getSpecialItems();
        } else if (mCurrentMode == SimulatorModeEnum.MORPH) {
            mainList = _getMorphItems();
        } else if (mCurrentMode == SimulatorModeEnum.ARMY) {
            mainList = _getArmyItems();
        }

        mMainList = mainList;
    }

    private List<SimulatorDataItem> _getMorphItems() {
        List<SimulatorDataItem> results = new ArrayList<>();

        if (mBuildOrder.getRace() != RaceEnum.Zerg) {
            return results;
        }

        for (BuildItemEntity item : mAllItems) {
            if (item.getItemType() != BuildItemTypeEnum.Unit || AppConstants.DEFAULT_STATE_ITEM_NAME.equals(item.getName())) {
                continue;
            }

            boolean satisfied = _isRequirementsSatisfied(item);

            SimulatorDataItem dataItem = _generateDataItem(item, satisfied, satisfied, 1);
            results.add(dataItem);
        }

        return results;
    }

    private List<SimulatorDataItem> _getBuildItems() {
        List<SimulatorDataItem> results = new ArrayList<>();

        for (BuildItemEntity item : mAllItems) {
            if (item.getItemType() != BuildItemTypeEnum.Building || AppConstants.DEFAULT_STATE_ITEM_NAME.equals(item.getName())) {
                continue;
            }

            boolean satisfied = _isRequirementsSatisfied(item);

            SimulatorDataItem dataItem = _generateDataItem(item, satisfied, satisfied, 1);
            results.add(dataItem);
        }

        return results;
    }

    private List<SimulatorDataItem> _getSpecialItems() {
        List<SimulatorDataItem> results = new ArrayList<>();

        for (BuildItemEntity item : mAllItems) {
            if (item.getItemType() != BuildItemTypeEnum.Special || item.getName().contains("Idle") || AppConstants.DEFAULT_STATE_ITEM_NAME.equals(item.getName())) {
                continue;
            }

            boolean satisfied = _isRequirementsSatisfied(item);

            SimulatorDataItem dataItem = _generateDataItem(item, satisfied, satisfied, 1);
            results.add(dataItem);
        }

        return results;
    }

    private List<SimulatorDataItem> _getArmyItems() {
        List<SimulatorDataItem> results = new ArrayList<>();

        BuildItemStatistics stats = mBuildProcessor.getCurrentStatistics();

        for (BuildItemEntity item : mAllItems) {
            if (item.getItemType() != BuildItemTypeEnum.Unit || AppConstants.DEFAULT_STATE_ITEM_NAME.equals(item.getName())) {
                continue;
            }

            int totalItemCount = stats.getStatValueByName(item.getName());

            if (totalItemCount > 0) {
                SimulatorDataItem dataItem = _generateDataItem(item, true, mBuildOrder.getRace() == RaceEnum.Zerg, totalItemCount);
                results.add(dataItem);
            }
        }

        return results;
    }

    private List<SimulatorDataItem> _getBaseItems() {
        List<SimulatorDataItem> results = new ArrayList<>();
        List<SimulatorDataItem> secondaryBuildings = new ArrayList<>();

        BuildItemStatistics stats = mBuildProcessor.getCurrentStatistics();

        for (BuildItemEntity item : mAllItems) {
            if ((item.getItemType() != BuildItemTypeEnum.Building && !item.getName().equals("SwitchToWarpgate"))
                    || AppConstants.DEFAULT_STATE_ITEM_NAME.equals(item.getName())
                    || item.getName().equals("OrbitalCommand")
                    || item.getName().equals("PlanetaryFortrees")
                    || item.getName().equals("GreaterSpire")
                    || item.getName().equals("Lair")
                    || item.getName().equals("Hive")) {
                continue;
            }

            int totalBuildingCount = stats.getStatValueByName(item.getName());
            int buzyBuildingCount = stats.getStatValueByName(item.getName() + EngineConsts.BUZY_BUILD_ITEM_POSTFIX);

            if (totalBuildingCount > 0 && (item.getName().equals("Refinery") || item.getName().equals("Assimilator") || item.getName().equals("Extractor"))) {
                boolean hasFreeSpace = _hasFreeSpaceOnGas(stats);
                SimulatorDataItem dataItem = _generateDataItem(item, true, hasFreeSpace, totalBuildingCount);
                secondaryBuildings.add(dataItem);
                continue;
            }

            if (item.getName().equals("Gateway")) {
                int freeWarpgates = stats.getStatValueByName("FreeGatewayForUnit");
                int buzyWarpgates = stats.getStatValueByName("FreeGatewayForUnit" + EngineConsts.BUZY_BUILD_ITEM_POSTFIX);

                // Active buildings first
                for (int i = 0; i < freeWarpgates - buzyWarpgates; i++) {
                    SimulatorDataItem dataItem = _generateDataItem(item, true, true, 1);
                    results.add(dataItem);
                }

                // Buzy buildings next
                for (int i = 0; i < buzyWarpgates;i++) {
                    SimulatorDataItem dataItem = _generateDataItem(item, false, false, 1);
                    results.add(dataItem);
                }

                continue;
            }

            if (item.getName().equals("CommandCenter")) {
                results.addAll(_getCommandCenters(stats));
                continue;
            }

            if (item.getName().equals("Hatchery")) {
                results.addAll(_getHatcheries(stats));
                continue;
            }

            if (item.getName().equals("Nexus")) {
                results.addAll(0, _getNexuses(stats));
                continue;
            }

            if (item.getName().equals("SwitchToWarpgate")) {
                int freeWarpgates = stats.getStatValueByName("FreeWarpgateForUnit");
                int buzyWarpgates = stats.getStatValueByName("FreeWarpgateForUnit" + EngineConsts.BUZY_BUILD_ITEM_POSTFIX);

                int morphingWarpgates = stats.getStatValueByName("SwitchToWarpgate" + EngineConsts.BUILD_ITEM_ON_BUILDING_POSTFIX);

                // Active buildings first
                for (int i = 0; i < freeWarpgates - buzyWarpgates; i++) {
                    SimulatorDataItem dataItem = _generateDataItem(item, true, true, 1);
                    dataItem.DisplayName = "Warpgate";
                    results.add(dataItem);
                }

                // Buzy buildings next
                for (int i = 0; i < (buzyWarpgates + morphingWarpgates);i++) {
                    SimulatorDataItem dataItem = _generateDataItem(item, false, false, 1);
                    dataItem.DisplayName = "Warpgate";
                    results.add(dataItem);
                }

                continue;
            }

            if (item.getName().equals("Spire")) {
                results.addAll(_getSpires(stats));
                continue;
            }

            boolean isProdBuilding = _isProductionItem(item.getName());

            if (!isProdBuilding && totalBuildingCount > 0) {
                SimulatorDataItem dataItem = _generateDataItem(item, true, false, totalBuildingCount);
                secondaryBuildings.add(dataItem);
                continue;
            }

            int totalReactors = stats.getStatValueByName("ReactorOn" + item.getName());
            int buzyReactors = stats.getStatValueByName("ReactorOn" + item.getName() + EngineConsts.BUZY_BUILD_ITEM_POSTFIX);

            int totalActiveBuildings = (totalBuildingCount + totalReactors) - (buzyBuildingCount + buzyReactors);

            if (totalActiveBuildings > totalBuildingCount) {
                totalActiveBuildings = totalBuildingCount;
            } else if (totalActiveBuildings < 0) {
                totalActiveBuildings = 0;
            }

            // Active buildings first
            for (int i = 0; i < totalActiveBuildings; i++) {
                SimulatorDataItem dataItem = _generateDataItem(item, true, true, 1);
                results.add(dataItem);
            }

            // Buzy buildings next
            for (int i = 0; i < totalBuildingCount - totalActiveBuildings;i++) {
                SimulatorDataItem dataItem = _generateDataItem(item, false, false, 1);
                buzyBuildingCount--;
                results.add(dataItem);
            }
        }

        results.addAll(secondaryBuildings);

        return results;
    }

    private List<SimulatorDataItem> _getSpires(BuildItemStatistics stats) {
        int totalSpires = stats.getStatValueByName("Spire");
        int totalGreaterSpires = stats.getStatValueByName("GreaterSpire");

        int freeSpires = stats.getStatValueByName("FreeSpireForUpgrades");

        BuildItemEntity spire = _getItemByName("Spire");
        BuildItemEntity greaterSpire = _getItemByName("GreaterSpire");

        List<BuildItemEntity> buildings = new ArrayList<>();

        for (int i = 0; i < totalGreaterSpires; i++) {
            buildings.add(greaterSpire);
        }

        for (int i = 0; i < totalSpires - totalGreaterSpires; i++) {
            buildings.add(spire);
        }

        List<SimulatorDataItem> results = new ArrayList<>();

        int currentBaseIndex = 0;

        int totalBuildingCount = buildings.size();
        int buzyBuildingCount = totalBuildingCount - freeSpires;

        // Active buildings first
        for (int i = 0; i < (totalBuildingCount - buzyBuildingCount); i++) {
            BuildItemEntity item = buildings.get(currentBaseIndex);
            currentBaseIndex++;

            SimulatorDataItem dataItem = _generateDataItem(item, true, true, 1);
            results.add(dataItem);
        }

        // Buzy buildings next
        for (int i = 0; i < buzyBuildingCount;i++) {
            BuildItemEntity item = buildings.get(currentBaseIndex);
            currentBaseIndex++;

            SimulatorDataItem dataItem = _generateDataItem(item, false, false, 1);
            results.add(dataItem);
        }

        return results;
    }

    private List<SimulatorDataItem> _getHatcheries(BuildItemStatistics stats) {
        int totalHatcheries = stats.getStatValueByName("Hatchery");
        int totalLairs = stats.getStatValueByName("Lair");
        int totalHives = stats.getStatValueByName("Hive");

        int freeHatcheries = stats.getStatValueByName("FreeHatcheryForUpgrades");

        int castsCount = stats.getStatValueByName("TotalCasts");

        BuildItemEntity hatchery = _getItemByName("Hatchery");
        BuildItemEntity lair = _getItemByName("Lair");
        BuildItemEntity hive = _getItemByName("Hive");

        List<BuildItemEntity> bases = new ArrayList<>();

        for (int i = 0; i < totalHives; i++) {
            bases.add(hive);
        }

        for (int i = 0; i < totalLairs - totalHives; i++) {
            bases.add(lair);
        }

        for (int i = 0; i < totalHatcheries - totalLairs - totalHives; i++) {
            bases.add(hatchery);
        }

        List<SimulatorDataItem> results = new ArrayList<>();

        int currentBaseIndex = 0;

        int totalBuildingCount = bases.size();
        int buzyBuildingCount = totalBuildingCount - freeHatcheries;

        // Active buildings first
        for (int i = 0; i < (totalBuildingCount - buzyBuildingCount); i++) {
            BuildItemEntity item = bases.get(currentBaseIndex);
            currentBaseIndex++;

            SimulatorDataItem dataItem = _generateDataItem(item, true, true, 1);
            results.add(dataItem);
        }

        // If no hatch available but it is possible to cast - we should add one available hatch
        if (results.size() == 0 && castsCount > 0) {
            SimulatorDataItem dataItem = _generateDataItem(hatchery, true, true, 1);
            buzyBuildingCount--;
            results.add(dataItem);
        }

        // Buzy buildings next
        for (int i = 0; i < buzyBuildingCount;i++) {
            BuildItemEntity item = bases.get(currentBaseIndex);
            currentBaseIndex++;

            SimulatorDataItem dataItem = _generateDataItem(item, false, false, 1);
            results.add(dataItem);
        }

        return results;
    }

    private List<SimulatorDataItem> _getNexuses(BuildItemStatistics stats) {
        int totalBuildingCount = stats.getStatValueByName("Nexus");
        int buzyBuildingCount = stats.getStatValueByName("Nexus" + EngineConsts.BUZY_BUILD_ITEM_POSTFIX);

        BuildItemEntity nexus = _getItemByName("Nexus");

        List<SimulatorDataItem> results = new ArrayList<>();

        // Active buildings first
        for (int i = 0; i < (totalBuildingCount - buzyBuildingCount); i++) {
            SimulatorDataItem dataItem = _generateDataItem(nexus, true, true, 1);
            results.add(dataItem);
        }

        // Buzy buildings next
        for (int i = 0; i < buzyBuildingCount;i++) {
            SimulatorDataItem dataItem = _generateDataItem(nexus, false, false, 1);
            results.add(dataItem);
        }

        return results;
    }

    private List<SimulatorDataItem> _getCommandCenters(BuildItemStatistics stats) {
        int totalBuildingCount = stats.getStatValueByName("CommandCenter");
        int buzyBuildingCount = stats.getStatValueByName("CommandCenter" + EngineConsts.BUZY_BUILD_ITEM_POSTFIX);
        int castsCount = stats.getStatValueByName("TotalCasts");

        int orbitals = stats.getStatValueByName("OrbitalCommand");
        int planetaries = stats.getStatValueByName("PlanetaryFortrees");

        BuildItemEntity commandCenter = _getItemByName("CommandCenter");
        BuildItemEntity orbitalCommand = _getItemByName("OrbitalCommand");
        BuildItemEntity planetaryFortrees = _getItemByName("PlanetaryFortrees");

        List<BuildItemEntity> bases = new ArrayList<>();

        for (int i = 0; i < orbitals; i++) {
            bases.add(orbitalCommand);
        }

        for (int i = 0; i < planetaries; i++) {
            bases.add(planetaryFortrees);
        }

        for (int i = 0; i < totalBuildingCount - (orbitals + planetaries); i++) {
            bases.add(commandCenter);
        }

        List<SimulatorDataItem> results = new ArrayList<>();

        int currentBaseIndex = 0;

        // Active buildings first
        for (int i = 0; i < (totalBuildingCount - buzyBuildingCount); i++) {
            BuildItemEntity item = bases.get(currentBaseIndex);
            currentBaseIndex++;

            SimulatorDataItem dataItem = _generateDataItem(item, true, true, 1);
            results.add(dataItem);
        }

        // If no CC available but it is possible to cast mule - we should add one available CC
        if (results.size() == 0 && castsCount > 0) {
            SimulatorDataItem dataItem = _generateDataItem(orbitalCommand, true, true, 1);
            buzyBuildingCount--;
            results.add(dataItem);
        }

        // Buzy buildings next
        for (int i = 0; i < buzyBuildingCount;i++) {
            BuildItemEntity item = bases.get(currentBaseIndex);
            currentBaseIndex++;

            SimulatorDataItem dataItem = _generateDataItem(item, false, false, 1);
            results.add(dataItem);
        }

        return results;
    }

    private boolean _hasFreeSpaceOnGas(BuildItemStatistics stats) {
        int refCount = 0;
        int sendWorkers = 0;
        int workersOnRefCount = stats.getWorkersOnGas();

        if (mBuildOrder.getRace() == RaceEnum.Terran) {
            refCount = stats.getStatValueByName("Refinery");
            sendWorkers = stats.getStatValueByName("GasScv" + EngineConsts.BUILD_ITEM_ON_BUILDING_POSTFIX);
        } else if (mBuildOrder.getRace() == RaceEnum.Protoss) {
            refCount = stats.getStatValueByName("Assimilator");
            sendWorkers = stats.getStatValueByName("GasProbe" + EngineConsts.BUILD_ITEM_ON_BUILDING_POSTFIX);
        } else if (mBuildOrder.getRace() == RaceEnum.Zerg) {
            refCount = stats.getStatValueByName("Extractor");
            sendWorkers = stats.getStatValueByName("GasDrone" + EngineConsts.BUILD_ITEM_ON_BUILDING_POSTFIX);
        }

        return refCount*3 > (workersOnRefCount+sendWorkers);
    }

    private SimulatorDataItem _generateDataItem(BuildItemEntity item, boolean isEnabled, boolean isClickable, int count) {
        SimulatorDataItem dataItem = new SimulatorDataItem();
        dataItem.Name = item.getName();
        dataItem.DisplayName = item.getDisplayName();
        dataItem.Type = item.getItemType();
        dataItem.IsEnabled = isEnabled;
        dataItem.IsClickable = isClickable;
        dataItem.Count = count;
        dataItem.Race = mBuildOrder.getRace();
        dataItem.Mode = mCurrentMode;

        return dataItem;
    }

    private void _runTimer() {
        mIsPlaying = true;
        // TODO: Separate setting for LOTV?
        mHandler.removeCallbacks(mTimerTick);
        int gameSpeed = _getGameSpeed();
        mHandler.postDelayed(mTimerTick, gameSpeed);
    }

    private void _initBuildOrder() {
        BuildOrderEntity mBuildEntity = new BuildOrderEntity("",
                BuildOrdersProvider.getInstance(mView.getContext()).getVersionFilter(),
                "",
                BuildOrdersProvider.getInstance(mView.getContext()).getFactionFilter(),
                RaceEnum.NotDefined,
                0,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                new ArrayList<String>());

        mBuildProcessor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(mBuildEntity, true);

        mBuildOrder = mBuildProcessor.getCurrentBuildOrder();
    }

    private int _getGameSpeed() {
        switch (mGameSpeed.toLowerCase()) {
            case "slower":
                return UiDataViewHelper.isVersionLotv(mBuildOrder.getsC2VersionID()) ? 2291 : 1662;
            case "slow":
                return UiDataViewHelper.isVersionLotv(mBuildOrder.getsC2VersionID()) ? 1725 : 1200;
            case "normal":
                return UiDataViewHelper.isVersionLotv(mBuildOrder.getsC2VersionID()) ? 1380 : 1000;
            case "fast":
                return UiDataViewHelper.isVersionLotv(mBuildOrder.getsC2VersionID()) ? 1141 : 820;
            default:
                return UiDataViewHelper.isVersionLotv(mBuildOrder.getsC2VersionID()) ? 970 : 719;
        }
    }

    private boolean _isRequirementsSatisfied(BuildItemEntity item) {
        boolean satisfied = true;
        for (IBuildItemRequirement requirement : item.getOrderRequirements()) {
            if (!requirement.isRequirementSatisfied(mBuildOrder.getLastBuildItem().getStatisticsProvider())) {
                satisfied = false;
                break;
            }
        }

        if (satisfied) {
            for (IBuildItemRequirement requirement : item.getProduceRequirements()) {
                if (!requirement.isRequirementSatisfied(mBuildOrder.getLastBuildItem().getStatisticsProvider())) {
                    satisfied = false;
                    break;
                }
            }
        }

        return satisfied;
    }
}
