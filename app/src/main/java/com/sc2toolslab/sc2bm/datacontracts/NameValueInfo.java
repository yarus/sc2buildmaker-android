package com.sc2toolslab.sc2bm.datacontracts;

import java.util.ArrayList;
import java.util.List;

public class NameValueInfo {
	private String Name;
	private String Value;

	private List<NameValueInfo> childInfos = new ArrayList<NameValueInfo>();

	public NameValueInfo() {

	}

	public NameValueInfo(String name, String value) {
		this.Name = name;
		this.Value = value;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		this.Name = name;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		this.Value = value;
	}

	public List<NameValueInfo> getChildInfos() {
		return childInfos;
	}

	public void setChildInfos(List<NameValueInfo> childInfos) {
		this.childInfos = childInfos;
	}
}
