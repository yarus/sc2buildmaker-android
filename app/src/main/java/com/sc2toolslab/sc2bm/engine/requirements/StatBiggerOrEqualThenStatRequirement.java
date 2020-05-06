package com.sc2toolslab.sc2bm.engine.requirements;

import com.sc2toolslab.sc2bm.datacontracts.ItemWithAttributesInfo;
import com.sc2toolslab.sc2bm.datacontracts.NameValueInfo;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;

public class StatBiggerOrEqualThenStatRequirement implements IBuildItemRequirement {
	private String leftStatName;
	private String rightStatName;

	public StatBiggerOrEqualThenStatRequirement(String leftStatName, String rightStatName) {
		this.leftStatName = leftStatName;
		this.rightStatName = rightStatName;
	}

	@Override
	public String getRequirementName() {
		return BuildItemRequirementFactory.STAT_BIGGER_OR_EQUAL_THAN_STAT_REQUIREMENT;
	}

	@Override
	public ItemWithAttributesInfo getAttributesInfo() {
		ItemWithAttributesInfo item = new ItemWithAttributesInfo();
		item.setName(getRequirementName());
		item.getAttributes().add(new NameValueInfo("LeftStatName", leftStatName));
		item.getAttributes().add(new NameValueInfo("RightStatName", rightStatName));

		return item;
	}

	@Override
	public boolean isRequirementSatisfied(BuildItemStatistics stats) {
		return stats.getStatValueByName(leftStatName) >= stats.getStatValueByName(rightStatName);
	}

	public String getLeftStatName() {
		return leftStatName;
	}
}
