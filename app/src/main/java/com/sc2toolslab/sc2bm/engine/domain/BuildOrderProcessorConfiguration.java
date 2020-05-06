package com.sc2toolslab.sc2bm.engine.domain;

import com.sc2toolslab.sc2bm.datacontracts.GlobalConstantsInfo;
import com.sc2toolslab.sc2bm.datacontracts.RaceConstantsInfo;
import com.sc2toolslab.sc2bm.domain.BuildItemsDictionary;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.modules.BuildManagerModulesList;

public class BuildOrderProcessorConfiguration {
	private String sC2VersionID;

	private RaceEnum race;

	private GlobalConstantsInfo globalConstants;

	private RaceConstantsInfo raceConstants;

	private BuildItemsDictionary buildItemsDictionary;

	private BuildManagerModulesList buildManagerModules;

	public String getsC2VersionID() {
		return sC2VersionID;
	}

	public void setsC2VersionID(String sC2VersionID) {
		this.sC2VersionID = sC2VersionID;
	}

	public RaceEnum getRace() {
		return race;
	}

	public void setRace(RaceEnum race) {
		this.race = race;
	}

	public GlobalConstantsInfo getGlobalConstants() {
		return globalConstants;
	}

	public void setGlobalConstants(GlobalConstantsInfo globalConstants) {
		this.globalConstants = globalConstants;
	}

	public RaceConstantsInfo getRaceConstants() {
		return raceConstants;
	}

	public void setRaceConstants(RaceConstantsInfo raceConstants) {
		this.raceConstants = raceConstants;
	}

	public BuildItemsDictionary getBuildItemsDictionary() {
		return buildItemsDictionary;
	}

	public void setBuildItemsDictionary(BuildItemsDictionary buildItemsDictionary) {
		this.buildItemsDictionary = buildItemsDictionary;
	}

	public BuildManagerModulesList getBuildManagerModules() {
		return buildManagerModules;
	}

	public void setBuildManagerModules(BuildManagerModulesList buildManagerModules) {
		this.buildManagerModules = buildManagerModules;
	}
}
