package com.sc2toolslab.sc2bm.datamanagers;

import com.sc2toolslab.sc2bm.datacontracts.BuildOrderInfo;
import com.sc2toolslab.sc2bm.datamanagers.interfaces.IBuildOrdersManager;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.dataaccess.interfaces.IBuildOrdersDataAccess;

import java.util.ArrayList;
import java.util.List;

public class BuildOrdersManager implements IBuildOrdersManager {
	private IBuildOrdersDataAccess mDataAccess;

	public BuildOrdersManager(IBuildOrdersDataAccess dataAccess) {
		this.mDataAccess = dataAccess;
	}

	public List<BuildOrderEntity> getBuildOrders() {
		List<BuildOrderEntity> result = new ArrayList<>();

		List<BuildOrderInfo> buildOrders = this.mDataAccess.getBuildOrders();

		for (BuildOrderInfo buildOrderInfo : buildOrders) {
			BuildOrderEntity entity = InfoEntityConverter.convert(buildOrderInfo);
			result.add(entity);
		}

		return result;
	}

	public void saveBuildOrder(BuildOrderEntity buildOrder) {
		long currentDate = System.currentTimeMillis();

		if (buildOrder.getCreationDate() == 0) {
			buildOrder.setCreationDate(currentDate);
		}

		buildOrder.setVisitedDate(currentDate);

		BuildOrderInfo info = InfoEntityConverter.convert(buildOrder);

		this.mDataAccess.saveBuildOrder(info);
	}
}
