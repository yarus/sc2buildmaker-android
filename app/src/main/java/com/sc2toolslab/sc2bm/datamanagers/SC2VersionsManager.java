package com.sc2toolslab.sc2bm.datamanagers;

import com.sc2toolslab.sc2bm.dataaccess.interfaces.ISC2VersionsDataAccess;
import com.sc2toolslab.sc2bm.datacontracts.SC2VersionInfo;
import com.sc2toolslab.sc2bm.datamanagers.interfaces.ISC2VersionsManager;
import com.sc2toolslab.sc2bm.domain.SC2VersionEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SC2VersionsManager implements ISC2VersionsManager {
	private ISC2VersionsDataAccess mDataAccess;

	private Map<String, SC2VersionEntity> mLoadedVersions;

	public SC2VersionsManager(ISC2VersionsDataAccess dataAccess) {
		this.mDataAccess = dataAccess;

		this.mLoadedVersions = new HashMap<String, SC2VersionEntity>();
	}

	public SC2VersionEntity getVersion(String versionID) {
		if (!this.mLoadedVersions.containsKey(versionID)) {
			SC2VersionInfo versionInfo = this.mDataAccess.getVersionInfo(versionID);
			if(versionInfo != null) {
				SC2VersionEntity versionEntity = InfoEntityConverter.convert(versionInfo);
				this.mLoadedVersions.put(versionEntity.getVersionID(), versionEntity);
			}
		}

		return this.mLoadedVersions.get(versionID);
	}

	public List<String> getSupportedVersionIDs() {
		return this.mDataAccess.getSupportedVersionIDs();
	}

	public void saveVersion(SC2VersionEntity versionEntity) {
		SC2VersionInfo info = InfoEntityConverter.convert(versionEntity);
		this.mDataAccess.saveSC2VersionInfo(info);
	}
}
