package com.sc2toolslab.sc2bm.dataaccess.interfaces;

import com.sc2toolslab.sc2bm.datacontracts.SC2VersionInfo;

import java.util.List;

public interface ISC2VersionsDataAccess {

	SC2VersionInfo getVersionInfo(String sc2VersionID);

	List<String> getSupportedVersionIDs();

	void saveSC2VersionInfo(SC2VersionInfo info);
}
