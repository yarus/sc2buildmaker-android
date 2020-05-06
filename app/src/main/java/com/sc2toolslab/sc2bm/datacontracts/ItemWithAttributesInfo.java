package com.sc2toolslab.sc2bm.datacontracts;

import java.util.ArrayList;
import java.util.List;

public class ItemWithAttributesInfo {
	private String Name;
	private List<NameValueInfo> Attributes = new ArrayList<NameValueInfo>();

	private List<ItemWithAttributesInfo> ChildItems = new ArrayList<ItemWithAttributesInfo>();

	public String ReadStringAttribute(String attributeName) {
		NameValueInfo attribute = null;
		for(NameValueInfo attr : Attributes) {
			if(attr.getName().equals(attributeName)) {
				attribute = attr;
				break;
			}
		}

		return attribute != null ? attribute.getValue() : "";
	}

	public Integer ReadIntAttribute(String attributeName) {
		String strValue = this.ReadStringAttribute(attributeName);

		int intValue = Integer.MIN_VALUE;
		if (strValue != null && !"".equals(strValue)) {
			try {
				intValue = Integer.parseInt(strValue);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		return intValue != Integer.MIN_VALUE ? intValue : null;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		this.Name = name;
	}

	public List<NameValueInfo> getAttributes() {
		return Attributes;
	}

	public void setAttributes(List<NameValueInfo> attributes) {
		Attributes = attributes;
	}

	public List<ItemWithAttributesInfo> getChildItems() {
		return ChildItems;
	}

	public void setChildItems(List<ItemWithAttributesInfo> childItems) {
		this.ChildItems = childItems;
	}
}
