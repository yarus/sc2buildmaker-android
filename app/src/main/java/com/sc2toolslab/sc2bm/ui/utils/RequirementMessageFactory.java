package com.sc2toolslab.sc2bm.ui.utils;

import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.BuildItemsDictionary;
import com.sc2toolslab.sc2bm.engine.EngineConsts;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;
import com.sc2toolslab.sc2bm.engine.requirements.BuildItemRequirementFactory;
import com.sc2toolslab.sc2bm.engine.requirements.ItemExistsOrOnBuildingRequirement;
import com.sc2toolslab.sc2bm.engine.requirements.OrBuildItemRequirement;
import com.sc2toolslab.sc2bm.engine.requirements.StatBiggerOrEqualThenStatRequirement;
import com.sc2toolslab.sc2bm.engine.requirements.StatBiggerOrEqualThenStatWithAdditionRequirement;
import com.sc2toolslab.sc2bm.engine.requirements.StatBiggerOrEqualThenValueRequirement;
import com.sc2toolslab.sc2bm.engine.requirements.StatLessThenStatRequirement;

public class RequirementMessageFactory {
	private RequirementMessageFactory() {
	}

	public static String getRequirementMessage(IBuildItemRequirement requirement, BuildItemsDictionary itemsDictionary, BuildItemEntity entity) {
		String reqName = requirement.getRequirementName();

		if (reqName.equals(BuildItemRequirementFactory.ITEM_EXISTS_OR_ON_BUILDING_REQUIREMENT)) {
			String targetItemName = ((ItemExistsOrOnBuildingRequirement) requirement).getTargetItemName();
			if (entity == null) {
				return targetItemName + " needed";
			} else {
				BuildItemEntity neededItem = itemsDictionary.getItem(targetItemName);
				if (neededItem != null) {
					return neededItem.getDisplayName() + " needed";
				} else {
					return targetItemName + " needed";
				}
			}
		}

		if (reqName.equals(BuildItemRequirementFactory.OR_BUILDING_ITEM_REQUIREMENT)) {
			String rightMessage = getRequirementMessage(((OrBuildItemRequirement) requirement).getRightBuildItemRequirement(), itemsDictionary, entity);
			String leftMessage = getRequirementMessage(((OrBuildItemRequirement) requirement).getLeftBuildItemRequirement(), itemsDictionary, entity);

			switch (((OrBuildItemRequirement) requirement).getUnsatisfiedRequirementId()) {
				case OrBuildItemRequirement.RIGHT_REQUIREMENT_UNSATISFIED:
					return rightMessage;
				case OrBuildItemRequirement.LEFT_REQUIREMENT_UNSATISFIED:
					return leftMessage;
				default:
					return rightMessage + " or " + leftMessage;
			}
		}

		if (reqName.equals(BuildItemRequirementFactory.STAT_BIGGER_OR_EQUAL_THAN_STAT_REQUIREMENT)) {
			String leftStatName = ((StatBiggerOrEqualThenStatRequirement) requirement).getLeftStatName();
			if (EngineConsts.CoreStatistics.BUILDING_NEW_SUPPLY.equals(leftStatName)) {
				return "Insufficient Supply";
			} else {
				return "Insufficient " + leftStatName;
			}
		}

		if (reqName.equals(BuildItemRequirementFactory.STAT_BIGGER_OR_EQUAL_THAN_STAT_WITH_ADDITION_REQUIREMENT)) {
			String leftStatName = ((StatBiggerOrEqualThenStatWithAdditionRequirement) requirement).getLeftStatName();
			if (EngineConsts.CoreStatistics.BUILDING_NEW_SUPPLY.equals(leftStatName)) {
				return "Insufficient Supply";
			} else {
				return "Insufficient " + leftStatName;
			}
		}

		if (reqName.equals(BuildItemRequirementFactory.STAT_BIGGER_OR_EQUAL_THAN_VALUE_REQUIREMENT)) {
			String leftStatName = ((StatBiggerOrEqualThenValueRequirement) requirement).getTargetStatName();
			if (EngineConsts.CoreStatistics.BUILDING_NEW_SUPPLY.equals(leftStatName)) {
				return "Insufficient Supply";
			} else {
				return "Insufficient " + leftStatName;
			}
		}

		if (reqName.equals(BuildItemRequirementFactory.STAT_LESS_THAN_STAT_REQUIREMENT)) {
			String target = ((StatLessThenStatRequirement) requirement).getLeftStatistic();
			return "Free " + target + " needed";
		}

		return "";
	}
}
