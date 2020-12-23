package com.sc2toolslab.sc2bm.engine.requirements;

import com.sc2toolslab.sc2bm.datacontracts.ItemWithAttributesInfo;
import com.sc2toolslab.sc2bm.datacontracts.NameValueInfo;
import com.sc2toolslab.sc2bm.engine.EngineConsts;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;

public class ItemExistsOrOnBuildingRequirement implements IBuildItemRequirement {
	private String targetItemName;

	public ItemExistsOrOnBuildingRequirement(String targetItemName) {
		this.targetItemName = targetItemName;
	}

	@Override
	public String getRequirementName() {
		return BuildItemRequirementFactory.ITEM_EXISTS_OR_ON_BUILDING_REQUIREMENT;
	}

	@Override
	public ItemWithAttributesInfo getAttributesInfo() {
		ItemWithAttributesInfo item = new ItemWithAttributesInfo();
		item.setName(getRequirementName());
		item.getAttributes().add(new NameValueInfo("TargetItemName", String.valueOf(targetItemName)));

		return item;
	}

	@Override
	public boolean isRequirementSatisfied(BuildItemStatistics stats) {
		int statValue = stats.getStatValueByName(targetItemName);
		int statBuildingValue = stats.getStatValueByName(targetItemName + EngineConsts.BUILD_ITEM_ON_BUILDING_POSTFIX);
		return statValue > 0 || statBuildingValue > 0;
	}

	public String getTargetItemName() {
		return targetItemName;
	}
}
