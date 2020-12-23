package com.sc2toolslab.sc2bm.ui.model;

import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;

import java.util.ArrayList;
import java.util.List;

public class SimulationResultsData {
    public int TotalSpentMinerals;
    public int TotalSpentGas;
    public int TotalUnspentMinerals;
    public int TotalUnspentGas;
    public int TotalMinedMinerals;
    public int TotalMinedGas;
    public int TotalIncome;
    public int TotalUnspent;
    public double AverageUnspent;
    public double AverageIncome;
    public double AverageSpendingQuotient;
    public double MineralsToGasRatio;
    public int BuildLength;
    public int CurrentSupply;
    public int ArmySupply;
    public int Workers;
    public int SupplyCapTime;
    public int MainSaturationTiming;
    public int NaturalSaturationTiming;
    public int ThirdSaturationTiming;

    public List<BuildItemEntity> Army;
    public List<BuildItemEntity> Buildings;
    public List<BuildItemEntity> Upgrades;

    public RaceEnum Race;
    public String SC2Version;
    public long CreatedAt;
    public long UpdatedAt;
    public String BuildName;

    public SimulationResultsData() {
        Army = new ArrayList<>();
        Buildings = new ArrayList<>();
        Upgrades = new ArrayList<>();
    }
}
