package com.sc2toolslab.sc2bm.engine.requirements;

import com.sc2toolslab.sc2bm.datacontracts.ItemWithAttributesInfo;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;

public class OrBuildItemRequirement implements IBuildItemRequirement {
	public static final int RIGHT_REQUIREMENT_UNSATISFIED = 0;
	public static final int LEFT_REQUIREMENT_UNSATISFIED = 1;

	private IBuildItemRequirement leftBuildItemRequirement;
	private IBuildItemRequirement rightBuildItemRequirement;

	private int unsatisfiedRequirement = 0;

	public OrBuildItemRequirement(IBuildItemRequirement leftBuildItemRequirement, IBuildItemRequirement rightBuildItemRequirement) {
		this.leftBuildItemRequirement = leftBuildItemRequirement;
		this.rightBuildItemRequirement = rightBuildItemRequirement;
	}

	@Override
	public String getRequirementName() {
		return BuildItemRequirementFactory.OR_BUILDING_ITEM_REQUIREMENT;
	}

	@Override
	public ItemWithAttributesInfo getAttributesInfo() {
		ItemWithAttributesInfo item = new ItemWithAttributesInfo();
		item.setName(getRequirementName());
		item.getChildItems().add(leftBuildItemRequirement.getAttributesInfo());
		item.getChildItems().add(rightBuildItemRequirement.getAttributesInfo());

		return item;
	}

	@Override
	public boolean isRequirementSatisfied(BuildItemStatistics stats) {
		if (leftBuildItemRequirement == null && rightBuildItemRequirement != null) {
			if(rightBuildItemRequirement.isRequirementSatisfied(stats)) {
				return true;
			} else {
				unsatisfiedRequirement = RIGHT_REQUIREMENT_UNSATISFIED;
				return false;
			}
		}

		if (leftBuildItemRequirement != null && rightBuildItemRequirement == null) {
			if(leftBuildItemRequirement.isRequirementSatisfied(stats)) {
				return true;
			} else {
				unsatisfiedRequirement = LEFT_REQUIREMENT_UNSATISFIED;
				return false;
			}
		}

		if (leftBuildItemRequirement != null && rightBuildItemRequirement != null) {
			return leftBuildItemRequirement.isRequirementSatisfied(stats) ||
					rightBuildItemRequirement.isRequirementSatisfied(stats);
		}

		return true;
	}

	public IBuildItemRequirement getLeftBuildItemRequirement() {
		return leftBuildItemRequirement;
	}

	public IBuildItemRequirement getRightBuildItemRequirement() {
		return rightBuildItemRequirement;
	}

	public int getUnsatisfiedRequirementId() {
		return unsatisfiedRequirement;
	}
}
