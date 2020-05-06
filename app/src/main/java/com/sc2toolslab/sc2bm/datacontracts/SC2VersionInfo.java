package com.sc2toolslab.sc2bm.datacontracts;

import java.util.List;

public class SC2VersionInfo {
	public String VersionID;

	public String AddonID;

	public GlobalConstantsInfo GlobalSettings;

	public List<RaceSettingsInfo> RaceSettingsList;

	public String getVersionID() {
		return VersionID;
	}

	public void setVersionID(String versionID) {
		this.VersionID = versionID;
	}

	public String getAddonID() {
		return AddonID;
	}

	public void setAddonID(String addonID) {
		AddonID = addonID;
	}

	public GlobalConstantsInfo getGlobalSettings() {
		return GlobalSettings;
	}

	public void setGlobalSettings(GlobalConstantsInfo globalSettings) {
		this.GlobalSettings = globalSettings;
	}

	public List<RaceSettingsInfo> getRaceSettingsList() {
		return RaceSettingsList;
	}

	public void setRaceSettingsList(List<RaceSettingsInfo> raceSettingsList) {
		this.RaceSettingsList = raceSettingsList;
	}
}
