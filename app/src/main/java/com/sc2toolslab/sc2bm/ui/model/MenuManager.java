package com.sc2toolslab.sc2bm.ui.model;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.constants.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class MenuManager {
	private MenuManager() {
	}

	public static List<NavDrawerMenuItem> getMenuItems() {
		List<NavDrawerMenuItem> menuItems = new ArrayList<>();

		int[] icons = { R.mipmap.ic_buy, R.mipmap.ic_rate, R.mipmap.ic_launcher, R.mipmap.ic_feedback, R.mipmap.ic_settings, R.mipmap.ic_question };
		String[] titles = { "Buy", "Rate", "sc2bm.com", "Feedback", "Settings", "About" };

		for (int i = 0; i < titles.length && i < icons.length; i++) {
			NavDrawerMenuItem item = new NavDrawerMenuItem();
			item.IconId = icons[i];
			item.Title = titles[i];

			if (item.Title.equals("Buy") && !AppConstants.IS_FREE) {
				continue;
			}

			menuItems.add(item);
		}

		return menuItems;
	}
}
