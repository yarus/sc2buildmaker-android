package com.sc2toolslab.sc2bm.dataaccess.interfaces;

import com.sc2toolslab.sc2bm.datacontracts.BuildOrderInfo;
import com.sc2toolslab.sc2bm.datacontracts.SC2VersionInfo;

import java.util.List;

public interface IStorageDataAccess {
	List<BuildOrderInfo> processDirectory (String targetDirectory);

	void saveToStorage(BuildOrderInfo item, String storageFolder, String fileName);

	SC2VersionInfo readVersionInfoFromFile(String path);

	BuildOrderInfo readBuildOrderFromFile(String path);

	void deleteFromStorage(String fileName);
}