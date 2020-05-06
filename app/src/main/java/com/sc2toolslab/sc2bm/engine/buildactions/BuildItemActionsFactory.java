package com.sc2toolslab.sc2bm.engine.buildactions;

import com.sc2toolslab.sc2bm.datacontracts.ItemWithAttributesInfo;
import com.sc2toolslab.sc2bm.datacontracts.NameValueInfo;
import com.sc2toolslab.sc2bm.domain.ApplicationException;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemAction;

public class BuildItemActionsFactory {
	public static final String CHANGE_STATISTIC_ACTION = "ChangeStatisticAction";

	public static IBuildItemAction constructAction(ItemWithAttributesInfo info) throws ApplicationException {
		if (info.getName().equals(CHANGE_STATISTIC_ACTION)) {
			String targetStatName = getAttributeValueByName(info, "TargetStatisticName");
			int changeValue = Integer.parseInt(getAttributeValueByName(info, "ChangeValue"));
			String limitStatisticName = getAttributeValueByName(info, "LimitStatisticName");

			ChangeStatisticAction result = new ChangeStatisticAction(targetStatName, changeValue, limitStatisticName);

			return result;
		}

		throw new ApplicationException(String.format("Action %s is not supported", info.getName()));
	}

	private static String getAttributeValueByName(ItemWithAttributesInfo info, String name) {
		for(NameValueInfo attr : info.getAttributes()) {
			if(attr.getName().equals(name)) {
				return attr.getValue();
			}
		}

		return "";
	}
}
