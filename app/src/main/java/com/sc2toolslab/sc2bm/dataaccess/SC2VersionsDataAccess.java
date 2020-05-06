package com.sc2toolslab.sc2bm.dataaccess;

import com.sc2toolslab.sc2bm.datacontracts.SC2VersionInfo;
import com.sc2toolslab.sc2bm.dataaccess.interfaces.ISC2VersionsDataAccess;
import com.sc2toolslab.sc2bm.dataaccess.interfaces.IStorageDataAccess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SC2VersionsDataAccess implements ISC2VersionsDataAccess {
	private static final String VERSIONS_FOLDER = "Versions//";
	private IStorageDataAccess mStorageDataAccess;

	public SC2VersionsDataAccess(IStorageDataAccess storageDataAccess) {
		mStorageDataAccess = storageDataAccess;
	}

	public SC2VersionInfo getVersionInfo(String sc2VersionID) {
		return mStorageDataAccess.readVersionInfoFromFile(FileStorageHelper.getBuildConfigurationsDirectory() + File.separator + sc2VersionID + ".txt");
	}

	public List<String> getSupportedVersionIDs() {
		List<String> fullFileNames = new ArrayList<String>();//mStorageDataAccess.getFileNamesInDirectory(VERSIONS_FOLDER);

		return fullFileNames;//fullFileNames.Select(p => p.Substring(VersionsFolder.Length, p.Length - p.IndexOf('.'))).ToList();
	}

	public void saveSC2VersionInfo(SC2VersionInfo info) {
		//mStorageDataAccess.SaveToStorage(info, VersionsFolder, info.VersionID);
	}
}
