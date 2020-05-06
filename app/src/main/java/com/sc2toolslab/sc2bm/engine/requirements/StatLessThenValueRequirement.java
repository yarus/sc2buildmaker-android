package com.sc2toolslab.sc2bm.engine.requirements;

import com.sc2toolslab.sc2bm.datacontracts.ItemWithAttributesInfo;
import com.sc2toolslab.sc2bm.datacontracts.NameValueInfo;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;

public class StatLessThenValueRequirement implements IBuildItemRequirement {
	private String targetStatisticName;
	private Integer value = 0;

	public StatLessThenValueRequirement(String targetStatisticName, Integer value) {
		this.targetStatisticName = targetStatisticName;
		this.value = value;
	}

	@Override
	public String getRequirementName() {
		return BuildItemRequirementFactory.STAT_LESS_THAN_VALUE_REQUIREMENT;
	}

	@Override
	public ItemWithAttributesInfo getAttributesInfo() {
		ItemWithAttributesInfo item = new ItemWithAttributesInfo();
		item.setName(getRequirementName());
		item.getAttributes().add(new NameValueInfo("TargetStatName", targetStatisticName));
		item.getAttributes().add(new NameValueInfo("Value", String.valueOf(value)));

		return item;
	}

	@Override
	public boolean isRequirementSatisfied(BuildItemStatistics stats) {
		return stats.getStatValueByName(targetStatisticName) < value;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}
