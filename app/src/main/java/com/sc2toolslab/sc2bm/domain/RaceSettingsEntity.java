package com.sc2toolslab.sc2bm.domain;

import com.sc2toolslab.sc2bm.datacontracts.RaceConstantsInfo;
import com.sc2toolslab.sc2bm.engine.modules.BuildManagerModulesList;

public class RaceSettingsEntity {
	public RaceEnum race;
	public RaceConstantsInfo constants;
	public BuildItemsDictionary itemsDictionary;
	public BuildManagerModulesList modules;

	public RaceSettingsEntity() {
		this.itemsDictionary = new BuildItemsDictionary();
		this.modules = new BuildManagerModulesList();
	}

	public RaceEnum getRace() {
		return race;
	}

	public void setRace(RaceEnum race) {
		this.race = race;
	}

	public RaceConstantsInfo getConstants() {
		return constants;
	}

	public void setConstants(RaceConstantsInfo constants) {
		this.constants = constants;
	}

	public BuildItemsDictionary getItemsDictionary() {
		return itemsDictionary;
	}

	public void setItemsDictionary(BuildItemsDictionary itemsDictionary) {
		this.itemsDictionary = itemsDictionary;
	}

	public BuildManagerModulesList getModules() {
		return modules;
	}

	public void setModules(BuildManagerModulesList modules) {
		this.modules = modules;
	}
}
