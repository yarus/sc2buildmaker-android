package com.sc2toolslab.sc2bm.engine.requirements;

import com.sc2toolslab.sc2bm.datacontracts.ItemWithAttributesInfo;
import com.sc2toolslab.sc2bm.datacontracts.NameValueInfo;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;

public class StatBiggerOrEqualThenValueRequirement implements IBuildItemRequirement {
	private String targetStatName;
	private Integer value = 0;

	public StatBiggerOrEqualThenValueRequirement(String targetStatName, Integer value) {
		this.targetStatName = targetStatName;
		this.value = value;
	}

	@Override
	public String getRequirementName() {
		return BuildItemRequirementFactory.STAT_BIGGER_OR_EQUAL_THAN_VALUE_REQUIREMENT;
	}

	@Override
	public ItemWithAttributesInfo getAttributesInfo() {
		ItemWithAttributesInfo item = new ItemWithAttributesInfo();
		item.setName(getRequirementName());
		item.getAttributes().add(new NameValueInfo("TargetStatName", targetStatName));
		item.getAttributes().add(new NameValueInfo("Value", String.valueOf(value)));

		return item;
	}

	@Override
	public boolean isRequirementSatisfied(BuildItemStatistics stats) {
		return stats.getStatValueByName(targetStatName) >= value;
	}

	public String getTargetStatName() {
		return targetStatName;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}
