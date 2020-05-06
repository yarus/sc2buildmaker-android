package com.sc2toolslab.sc2bm.dataaccess;

import com.sc2toolslab.sc2bm.datacontracts.BuildOrderInfo;
import com.sc2toolslab.sc2bm.dataaccess.interfaces.IBuildOrdersDataAccess;
import com.sc2toolslab.sc2bm.dataaccess.interfaces.IStorageDataAccess;

import java.util.List;

public class BuildOrdersDataAccess implements IBuildOrdersDataAccess {
	private IStorageDataAccess mStorageDataAccess;

	public BuildOrdersDataAccess(IStorageDataAccess storageDataAccess) {
		this.mStorageDataAccess = storageDataAccess;
	}

	public List<BuildOrderInfo> getBuildOrders() {
		return this.mStorageDataAccess.processDirectory(FileStorageHelper.getBuildOrdersDirectory());
	}

	public void saveBuildOrder(BuildOrderInfo bo) {
		this.mStorageDataAccess.saveToStorage(bo, FileStorageHelper.getBuildOrdersDirectory(), bo.getName());
	}
}
