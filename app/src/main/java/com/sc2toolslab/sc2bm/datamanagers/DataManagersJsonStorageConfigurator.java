package com.sc2toolslab.sc2bm.datamanagers;

import com.sc2toolslab.sc2bm.dataaccess.BuildOrdersDataAccess;
import com.sc2toolslab.sc2bm.dataaccess.JsonStorageDataAccess;
import com.sc2toolslab.sc2bm.dataaccess.SC2VersionsDataAccess;
import com.sc2toolslab.sc2bm.datamanagers.interfaces.IBuildOrdersManager;
import com.sc2toolslab.sc2bm.datamanagers.interfaces.IDataManagersConfigurator;
import com.sc2toolslab.sc2bm.datamanagers.interfaces.ISC2VersionsManager;

public class DataManagersJsonStorageConfigurator implements IDataManagersConfigurator {
	private IBuildOrdersManager mBuildOrderManager;

	private ISC2VersionsManager mSC2VersionsManager;

	public IBuildOrdersManager getBuildOrdersManager() {
		if (mBuildOrderManager == null) {
			mBuildOrderManager = new BuildOrdersManager(new BuildOrdersDataAccess(new JsonStorageDataAccess()));
		}

		return mBuildOrderManager;
	}

	public ISC2VersionsManager getSC2VersionsManager() {
		if (mSC2VersionsManager == null) {
			mSC2VersionsManager = new SC2VersionsManager(new SC2VersionsDataAccess(new JsonStorageDataAccess()));
		}

		return mSC2VersionsManager;
	}
}
