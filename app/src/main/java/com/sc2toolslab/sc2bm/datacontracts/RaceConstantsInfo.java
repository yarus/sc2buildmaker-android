package com.sc2toolslab.sc2bm.datacontracts;

import java.io.Serializable;

public class RaceConstantsInfo implements Serializable {
	public String DefaultBaseBuildingName;

	public String EnergyGeneratorBuildItemName;

	public int EnergyCastGenerateTime;

	public int EnergyCastLimitPerEnergyGenerator;

	public int EnergyCastCountForNewEnergyGenerator;

	public String getDefaultBaseBuildingName() {
		return DefaultBaseBuildingName;
	}

	public void setDefaultBaseBuildingName(String defaultBaseBuildingName) {
		this.DefaultBaseBuildingName = defaultBaseBuildingName;
	}

	public String getEnergyGeneratorBuildItemName() {
		return EnergyGeneratorBuildItemName;
	}

	public void setEnergyGeneratorBuildItemName(String energyGeneratorBuildItemName) {
		EnergyGeneratorBuildItemName = energyGeneratorBuildItemName;
	}

	public int getEnergyCastGenerateTime() {
		return EnergyCastGenerateTime;
	}

	public void setEnergyCastGenerateTime(int energyCastGenerateTime) {
		this.EnergyCastGenerateTime = energyCastGenerateTime;
	}

	public int getEnergyCastLimitPerEnergyGenerator() {
		return EnergyCastLimitPerEnergyGenerator;
	}

	public void setEnergyCastLimitPerEnergyGenerator(int energyCastLimitPerEnergyGenerator) {
		this.EnergyCastLimitPerEnergyGenerator = energyCastLimitPerEnergyGenerator;
	}

	public int getEnergyCastCountForNewEnergyGenerator() {
		return EnergyCastCountForNewEnergyGenerator;
	}

	public void setEnergyCastCountForNewEnergyGenerator(int energyCastCountForNewEnergyGenerator) {
		this.EnergyCastCountForNewEnergyGenerator = energyCastCountForNewEnergyGenerator;
	}
}
