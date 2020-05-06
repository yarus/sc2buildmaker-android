package com.sc2toolslab.sc2bm.dataaccess.interfaces;

import com.sc2toolslab.sc2bm.datacontracts.BuildOrderInfo;

import java.util.List;

public interface IBuildOrdersDataAccess {

	List<BuildOrderInfo> getBuildOrders();

	void saveBuildOrder(BuildOrderInfo bo);
}
