package com.sc2toolslab.sc2bm.dataaccess;

import java.io.File;

public class ConfigurationsHelper {
	public static String[] getConfigurationNames() {
		File dir = new File(FileStorageHelper.getBuildConfigurationsDirectory());
		if(dir.exists()) {
			String[] names = new String[dir.list().length];
			int i = 0;
			for(String name : dir.list()) {
				names[i] = name.substring(0, name.length() - 4);
				i++;
			}

			return names;
		}

		return new String[]{};
	}

	public static boolean checkIfConfigExists(String versionId) {
		for(String name : getConfigurationNames()) {
			if(name.equals(versionId)) {
				return true;
			}
		}

		return false;
	}
}
