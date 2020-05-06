package com.sc2toolslab.sc2bm.engine.requirements;

import com.sc2toolslab.sc2bm.datacontracts.ItemWithAttributesInfo;
import com.sc2toolslab.sc2bm.datacontracts.NameValueInfo;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;

public class LarvaRequirement implements IBuildItemRequirement {
	public int requiredValue;

	public LarvaRequirement(int requiredValue) {
		this.requiredValue = requiredValue;
	}

	@Override
	public String getRequirementName() {
		return BuildItemRequirementFactory.LARVA_REQUIREMENT;
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
		return stats.getStatValueByName("TotalLarva") >= requiredValue;
	}

	public int getRequiredValue() {
		return requiredValue;
	}
}
