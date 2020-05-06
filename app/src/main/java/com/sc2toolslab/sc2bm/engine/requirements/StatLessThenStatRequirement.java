package com.sc2toolslab.sc2bm.engine.requirements;

import com.sc2toolslab.sc2bm.datacontracts.ItemWithAttributesInfo;
import com.sc2toolslab.sc2bm.datacontracts.NameValueInfo;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;

public class StatLessThenStatRequirement implements IBuildItemRequirement {
	public String leftStatistic;
	public String rightStatistic;

	public StatLessThenStatRequirement(String leftStatistic, String rightStatistic) {
		this.leftStatistic = leftStatistic;
		this.rightStatistic = rightStatistic;
	}

	@Override
	public String getRequirementName() {
		return BuildItemRequirementFactory.STAT_LESS_THAN_STAT_REQUIREMENT;
	}

	@Override
	public ItemWithAttributesInfo getAttributesInfo() {
		ItemWithAttributesInfo item = new ItemWithAttributesInfo();
		item.setName(getRequirementName());
		item.getAttributes().add(new NameValueInfo("LeftStatistic", leftStatistic));
		item.getAttributes().add(new NameValueInfo("RightStatistic", rightStatistic));

		return item;
	}

	@Override
	public boolean isRequirementSatisfied(BuildItemStatistics stats) {
		return stats.getStatValueByName(leftStatistic) < stats.getStatValueByName(rightStatistic);
	}

	public String getLeftStatistic() {
		return leftStatistic;
	}
}
