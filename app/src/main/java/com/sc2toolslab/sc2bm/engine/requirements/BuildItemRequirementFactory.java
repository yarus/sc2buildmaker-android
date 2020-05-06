package com.sc2toolslab.sc2bm.engine.requirements;

import com.sc2toolslab.sc2bm.datacontracts.ItemWithAttributesInfo;
import com.sc2toolslab.sc2bm.domain.ApplicationException;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;

import java.util.Arrays;

public class BuildItemRequirementFactory {
	public static final String CAST_REQUIREMENT = "CastRequirement";
	public static final String ITEM_EXISTS_OR_ON_BUILDING_REQUIREMENT = "ItemExistsOrOnBuildingRequirement";
	public static final String LARVA_REQUIREMENT = "LarvaRequirement";
	public static final String OR_BUILDING_ITEM_REQUIREMENT = "OrBuildItemRequirement";
	public static final String STAT_BIGGER_OR_EQUAL_THAN_STAT_REQUIREMENT = "StatBiggerOrEqualThenStatRequirement";
	public static final String STAT_BIGGER_OR_EQUAL_THAN_STAT_WITH_ADDITION_REQUIREMENT = "StatBiggerOrEqualThenStatWithAdditionRequirement";
	public static final String STAT_BIGGER_OR_EQUAL_THAN_VALUE_REQUIREMENT = "StatBiggerOrEqualThenValueRequirement";
	public static final String STAT_LESS_THAN_STAT_REQUIREMENT = "StatLessThenStatRequirement";
	public static final String STAT_LESS_THAN_VALUE_REQUIREMENT = "StatLessThenValueRequirement";
	public static final String STATS_ADDITION_IS_BIGGER_THAN_STATS_ADDITION_REQUIREMENT = "StatsAdditionIsBiggerThenStatsAdditionRequirement";

	public static IBuildItemRequirement constructRequirement(ItemWithAttributesInfo info) throws ApplicationException {
		if (info.getName().equals(CAST_REQUIREMENT)) {
			int requiredValue = info.ReadIntAttribute("RequiredValue");

			return new CastRequirement(requiredValue);
		}

		if (info.getName().equals(ITEM_EXISTS_OR_ON_BUILDING_REQUIREMENT)) {
			String targetItemName = info.ReadStringAttribute("TargetItemName");

			return new ItemExistsOrOnBuildingRequirement(targetItemName);
		}

		if (info.getName().equals(LARVA_REQUIREMENT)) {
			int requiredValue = info.ReadIntAttribute("RequiredValue");

			return new LarvaRequirement(requiredValue);
		}

		if (info.getName().equals(OR_BUILDING_ITEM_REQUIREMENT)) {
			ItemWithAttributesInfo left = info.getChildItems().get(0);
			ItemWithAttributesInfo right = info.getChildItems().get(1);

			IBuildItemRequirement leftReq = constructRequirement(left);
			IBuildItemRequirement rightReq = constructRequirement(right);

			return new OrBuildItemRequirement(leftReq, rightReq);
		}

		if (info.getName().equals(STAT_BIGGER_OR_EQUAL_THAN_STAT_REQUIREMENT)) {
			String leftStatName = info.ReadStringAttribute("LeftStatName");
			String rightStatName = info.ReadStringAttribute("RightStatName");

			return new StatBiggerOrEqualThenStatRequirement(leftStatName, rightStatName);
		}

		if (info.getName().equals(STAT_BIGGER_OR_EQUAL_THAN_STAT_WITH_ADDITION_REQUIREMENT)) {
			String leftStatName = info.ReadStringAttribute("LeftStatName");
			String rightStatName = info.ReadStringAttribute("RightStatName");
			int additionValue = info.ReadIntAttribute("AdditionValue");

			return new StatBiggerOrEqualThenStatWithAdditionRequirement(leftStatName, rightStatName, additionValue);
		}

		if (info.getName().equals(STAT_BIGGER_OR_EQUAL_THAN_VALUE_REQUIREMENT)) {
			String targetStatName = info.ReadStringAttribute("TargetStatName");
			int value = info.ReadIntAttribute("Value");

			return new StatBiggerOrEqualThenValueRequirement(targetStatName, value);
		}

		if (info.getName().equals(STAT_LESS_THAN_STAT_REQUIREMENT)) {
			String leftStatName = info.ReadStringAttribute("LeftStatistic");
			String rightStatName = info.ReadStringAttribute("RightStatistic");

			return new StatLessThenStatRequirement(leftStatName, rightStatName);
		}

		if (info.getName().equals(STAT_LESS_THAN_VALUE_REQUIREMENT)) {
			String targetStatName = info.ReadStringAttribute("TargetStatName");
			int value = info.ReadIntAttribute("Value");

			return new StatLessThenValueRequirement(targetStatName, value);
		}

		if (info.getName().equals(STATS_ADDITION_IS_BIGGER_THAN_STATS_ADDITION_REQUIREMENT)) {
			String[] leftStat = info.ReadStringAttribute("LeftAddition").split(",");
			String[] rightStat = info.ReadStringAttribute("RightAddition").split(",");

			return new StatsAdditionIsBiggerThenStatsAdditionRequirement(Arrays.asList(leftStat), Arrays.asList(rightStat));
		}

		throw new ApplicationException(String.format("Requirement '{0}' is not supported", info.getName()));
	}
}