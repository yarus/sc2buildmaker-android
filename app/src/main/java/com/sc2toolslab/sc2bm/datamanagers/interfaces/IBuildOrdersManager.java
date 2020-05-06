package com.sc2toolslab.sc2bm.datamanagers.interfaces;

import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;

import java.util.List;

public interface IBuildOrdersManager {
	List<BuildOrderEntity> getBuildOrders();
	void saveBuildOrder(BuildOrderEntity entity);
}
