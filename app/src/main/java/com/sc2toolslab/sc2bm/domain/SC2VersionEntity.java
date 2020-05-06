package com.sc2toolslab.sc2bm.domain;

import com.sc2toolslab.sc2bm.datacontracts.GlobalConstantsInfo;

public class SC2VersionEntity {
	public String versionID;

	public String AddonID;

	public GlobalConstantsInfo globalConstants;

	public RaceSettingsEntityDictionary raceSettingsDictionary;

	public String getVersionID() {
		return versionID;
	}

	public void setVersionID(String versionID) {
		this.versionID = versionID;
	}

	public String getAddonID() {
		return AddonID;
	}

	public void setAddonID(String addonID) {
		AddonID = addonID;
	}

	public GlobalConstantsInfo getGlobalConstants() {
		return globalConstants;
	}

	public void setGlobalConstants(GlobalConstantsInfo globalConstants) {
		this.globalConstants = globalConstants;
	}

	public RaceSettingsEntityDictionary getRaceSettingsDictionary() {
		return raceSettingsDictionary;
	}

	public void setRaceSettingsDictionary(RaceSettingsEntityDictionary raceSettingsDictionary) {
		this.raceSettingsDictionary = raceSettingsDictionary;
	}
}
