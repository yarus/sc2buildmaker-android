package com.sc2toolslab.sc2bm.datacontracts;

import java.util.ArrayList;
import java.util.List;

public class RaceSettingsInfo {
	public String Race;
	public RaceConstantsInfo Constants;
	public List<BuildItemInfo> BuildItems = new ArrayList<BuildItemInfo>();
	public List<String> RaceModules = new ArrayList<String>();

	public String getRace() {
		return Race;
	}

	public void setRace(String race) {
		this.Race = race;
	}

	public RaceConstantsInfo getConstants() {
		return Constants;
	}

	public void setConstants(RaceConstantsInfo constants) {
		this.Constants = constants;
	}

	public List<BuildItemInfo> getBuildItems() {
		return BuildItems;
	}

	public void setBuildItems(List<BuildItemInfo> buildItems) {
		this.BuildItems = buildItems;
	}

	public List<String> getRaceModules() {
		return RaceModules;
	}

	public void setRaceModules(List<String> raceModules) {
		this.RaceModules = raceModules;
	}
}
