package com.sc2toolslab.sc2bm.engine.requirements;

import com.sc2toolslab.sc2bm.datacontracts.ItemWithAttributesInfo;
import com.sc2toolslab.sc2bm.datacontracts.NameValueInfo;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;

import java.util.List;

public class StatsAdditionIsBiggerThenStatsAdditionRequirement implements IBuildItemRequirement {
	private List<String> leftAddition;
	public List<String> rightAddition;

	public StatsAdditionIsBiggerThenStatsAdditionRequirement(List<String> leftAddition, List<String> rightAddition) {
		this.leftAddition = leftAddition;
		this.rightAddition = rightAddition;
	}

	private int CalculateAddition(BuildItemStatistics statistics, List<String> statsToAddition) {
		int sum = 0;
		for(String name : statsToAddition) {
			sum += statistics.getStatValueByName(name);
		}

		return sum;
	}

	@Override
	public String getRequirementName() {
		return BuildItemRequirementFactory.STATS_ADDITION_IS_BIGGER_THAN_STATS_ADDITION_REQUIREMENT;
	}

	@Override
	public ItemWithAttributesInfo getAttributesInfo() {
		ItemWithAttributesInfo item = new ItemWithAttributesInfo();
		item.setName(getRequirementName());
		item.getAttributes().add(new NameValueInfo("LeftAddition", getStatsList(leftAddition)));
		item.getAttributes().add(new NameValueInfo("RightAddition", getStatsList(rightAddition)));

		return item;
	}

	private String getStatsList(List<String> additionsList) {
		String result = "";
		for(String addition : additionsList) {
			result = result + addition + ",";
		}
		result = result.substring(0, result.length() - 1);

		return result;
	}

	@Override
	public boolean isRequirementSatisfied(BuildItemStatistics stats) {
		return CalculateAddition(stats, leftAddition) > CalculateAddition(stats, rightAddition);
	}
}
