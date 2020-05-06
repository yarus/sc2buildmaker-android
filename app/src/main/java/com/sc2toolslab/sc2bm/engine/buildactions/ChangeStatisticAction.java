package com.sc2toolslab.sc2bm.engine.buildactions;

import com.sc2toolslab.sc2bm.datacontracts.ItemWithAttributesInfo;
import com.sc2toolslab.sc2bm.datacontracts.NameValueInfo;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemAction;

import java.util.ArrayList;
import java.util.List;

public class ChangeStatisticAction implements IBuildItemAction {
	private String targetStatisticName;
	private Integer changeValue;
	private String limitStatisticName;

	public ChangeStatisticAction(String targetStatisticName, Integer changeValue) {
		this(targetStatisticName, changeValue, "");
	}

	protected ChangeStatisticAction(String targetStatisticName, Integer changeValue, String limitStatisticName)
	{
		this.targetStatisticName = targetStatisticName;
		this.changeValue = changeValue;
		this.limitStatisticName = limitStatisticName;
	}

	public void doAction(BuildItemStatistics statistics) {
		int valueToAdd = changeValue;

		if (limitStatisticName != null && !"".equals(limitStatisticName)) {
			int previousValue = statistics.getStatValueByName(targetStatisticName);

			int limitStatValue = statistics.getStatValueByName(limitStatisticName);
			if (previousValue + changeValue > limitStatValue) {
				valueToAdd = limitStatValue - previousValue;
			}
		}

		statistics.changeItemCountForName(targetStatisticName, valueToAdd);
	}

	@Override
	public String getActionName() {
		return BuildItemActionsFactory.CHANGE_STATISTIC_ACTION;
	}

	@Override
	public ItemWithAttributesInfo getAttributesInfo() {
		ItemWithAttributesInfo info = new ItemWithAttributesInfo();
		info.setName(getActionName());

		List<NameValueInfo> result = new ArrayList<>();
		result.add(new NameValueInfo("TargetStatisticName", targetStatisticName));
		result.add(new NameValueInfo("ChangeValue", String.valueOf(changeValue)));
		result.add(new NameValueInfo("LimitStatisticName", limitStatisticName));

		info.setAttributes(result);

		return info;
	}

	public String getTargetStatisticName() {
		return targetStatisticName;
	}

	public void setTargetStatisticName(String targetStatisticName) {
		this.targetStatisticName = targetStatisticName;
	}

	public Integer getChangeValue() {
		return changeValue;
	}

	public void setChangeValue(Integer changeValue) {
		this.changeValue = changeValue;
	}

	public String getLimitStatisticName() {
		return limitStatisticName;
	}

	public void setLimitStatisticName(String limitStatisticName) {
		this.limitStatisticName = limitStatisticName;
	}
}
