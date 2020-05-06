package com.sc2toolslab.sc2bm.engine.requirements;

import com.sc2toolslab.sc2bm.datacontracts.ItemWithAttributesInfo;
import com.sc2toolslab.sc2bm.datacontracts.NameValueInfo;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;
import com.sc2toolslab.sc2bm.engine.modules.CastModule;

public class CastRequirement implements IBuildItemRequirement {
	public int requiredValue;

	public CastRequirement(int requiredValue) {
		this.requiredValue = requiredValue;
	}

	@Override
	public String getRequirementName() {
		return BuildItemRequirementFactory.CAST_REQUIREMENT;
	}

	@Override
	public ItemWithAttributesInfo getAttributesInfo() {
		ItemWithAttributesInfo item = new ItemWithAttributesInfo();
		item.setName(getRequirementName());
		item.getAttributes().add(new NameValueInfo("RequiredValue", String.valueOf(requiredValue)));

		return item;
	}

	@Override
	public boolean isRequirementSatisfied(BuildItemStatistics stats) {
		return stats.getStatValueByName(CastModule.TOTAL_CASTS) >= requiredValue;
	}
}
