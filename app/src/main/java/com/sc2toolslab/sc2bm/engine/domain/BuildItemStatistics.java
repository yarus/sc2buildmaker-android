package com.sc2toolslab.sc2bm.engine.domain;

import com.sc2toolslab.sc2bm.engine.EngineConsts;
import com.sc2toolslab.sc2bm.datacontracts.RaceConstantsInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BuildItemStatistics implements Serializable {
	private Map<String, Integer> statValuesDictionary = new HashMap<String, Integer>();
	private RaceConstantsInfo raceConstants;

	private Integer currentSupply = 0;
	private Integer maximumSupply = 0;
	private Integer workesOnMinerals = 0;
	private Integer workersOnGas = 0;
	private Integer workersOnWalk = 0;
	private Integer basesCount = 0;
	private Integer workersCount = 0;
	private Integer minerals = 0;
	private Integer gas = 0;
	private Integer energy = 0;
	private Integer milliMinerals = 0;
	private Integer milliGas = 0;
	private Integer milliEnergy = 0;
	private Integer mulesOnMinerals = 0;

	public BuildItemStatistics(RaceConstantsInfo raceSettings) {
		this(raceSettings, new HashMap<String, Integer>());
	}

	public BuildItemStatistics(RaceConstantsInfo raceSettings, Map<String,Integer> statValuesDictionary) {
		this.raceConstants = raceSettings;
		this.statValuesDictionary = statValuesDictionary;
	}

	public Map<String, Integer> getStatsWithKeyContains(String filter) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		for(String key : statValuesDictionary.keySet()) {
			if(key.contains(filter)) {
				result.put(key, statValuesDictionary.get(key));
			}
		}
		return result;
	}

	public int getStatValueByName(String name) {
		if (this.statValuesDictionary.containsKey(name))
		{
			return this.statValuesDictionary.get(name);
		}

		return 0;
	}

	public void removeStat(String name) {
		statValuesDictionary.remove(name);
	}

	public void setItemCountForName(String name, int value) {
		statValuesDictionary.put(name, value);
	}

	public void changeItemCountForName(String name, int changeValue) {
		this.statValuesDictionary.put(name, getStatValueByName(name) + changeValue);
	}

	public Map<String, Integer> cloneItemsCountDictionary()
	{
		return new HashMap<String, Integer>(this.statValuesDictionary);
	}

	public Integer getCurrentSupply() {
		return getStatValueByName(EngineConsts.CoreStatistics.CURRENT_SUPPLY);
	}

	public void setCurrentSupply(Integer currentSupply) {
		statValuesDictionary.put(EngineConsts.CoreStatistics.CURRENT_SUPPLY, currentSupply);
	}

	public Integer getMaximumSupply() {
		return getStatValueByName(EngineConsts.CoreStatistics.MAXIMUM_SUPPLY);
	}

	public void setMaximumSupply(Integer maximumSupply) {
		statValuesDictionary.put(EngineConsts.CoreStatistics.MAXIMUM_SUPPLY, maximumSupply);
	}

	public Integer getWorkesOnMinerals() {
		return getStatValueByName(EngineConsts.CoreStatistics.WORKERS_ON_MINERALS);
	}

	public void setWorkesOnMinerals(Integer workesOnMinerals) {
		statValuesDictionary.put(EngineConsts.CoreStatistics.WORKERS_ON_MINERALS, workesOnMinerals);
	}

	public Integer getWorkersOnGas() {
		return getStatValueByName(EngineConsts.CoreStatistics.WORKERS_ON_GAS);
	}

	public void setWorkersOnGas(Integer workersOnGas) {
		statValuesDictionary.put(EngineConsts.CoreStatistics.WORKERS_ON_GAS, workersOnGas);
	}

	public Integer getWorkersOnWalk() {
		return getStatValueByName(EngineConsts.CoreStatistics.WORKERS_ON_WALK);
	}

	public void setWorkersOnWalk(Integer workersOnWalk) {
		statValuesDictionary.put(EngineConsts.CoreStatistics.WORKERS_ON_WALK, workersOnWalk);
	}

	public Integer getBasesCount() {
		return getStatValueByName(raceConstants.getDefaultBaseBuildingName());
	}

	public void setBasesCount(Integer basesCount) {
		statValuesDictionary.put(raceConstants.getDefaultBaseBuildingName(), basesCount);
	}

	public Integer getWorkersCount() {
		return getStatValueByName(EngineConsts.CoreStatistics.WORKERS_COUNT);
	}

	public void setWorkersCount(Integer workersCount) {
		statValuesDictionary.put(EngineConsts.CoreStatistics.WORKERS_COUNT, workersCount);
	}

	public Integer getMinerals() {
		return getStatValueByName(EngineConsts.CoreStatistics.MINERALS);
	}

	public void setMinerals(Integer minerals) {
		statValuesDictionary.put(EngineConsts.CoreStatistics.MINERALS, minerals);
	}

	public Integer getGas() {
		return getStatValueByName(EngineConsts.CoreStatistics.GAS);
	}

	public void setGas(Integer gas) {
		statValuesDictionary.put(EngineConsts.CoreStatistics.GAS, gas);
	}

	public Integer getMilliMinerals() {
		return getStatValueByName(EngineConsts.CoreStatistics.MILLI_MINERALS);
	}

	public void setMilliMinerals(Integer minerals) {
		statValuesDictionary.put(EngineConsts.CoreStatistics.MILLI_MINERALS, minerals);
	}

	public Integer getMilliGas() {
		return getStatValueByName(EngineConsts.CoreStatistics.MILLI_GAS);
	}

	public void setMilliGas(Integer gas) {
		statValuesDictionary.put(EngineConsts.CoreStatistics.MILLI_GAS, gas);
	}

	public Integer getMilliEnergy() {
		return getStatValueByName(EngineConsts.CoreStatistics.MILLI_ENERGY);
	}

	public void setMilliEnergy(Integer energy) {
		statValuesDictionary.put(EngineConsts.CoreStatistics.MILLI_ENERGY, energy);
	}

	public Integer getMulesOnMinerals() {
		return getStatValueByName(EngineConsts.CoreStatistics.MULES_ON_MINERALS);
	}

	public void setMulesOnMinerals(Integer mules) {
		statValuesDictionary.put(EngineConsts.CoreStatistics.MULES_ON_MINERALS, mules);
	}
}
