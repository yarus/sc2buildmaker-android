package com.sc2toolslab.sc2bm.dataaccess;

import com.google.gson.Gson;
import com.sc2toolslab.sc2bm.datacontracts.BuildOrderInfo;
import com.sc2toolslab.sc2bm.datacontracts.SC2VersionInfo;
import com.sc2toolslab.sc2bm.dataaccess.interfaces.IStorageDataAccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JsonStorageDataAccess implements IStorageDataAccess {
	@Override
	public List<BuildOrderInfo> processDirectory(String targetDirectory) {
		List<BuildOrderInfo> result = new ArrayList<BuildOrderInfo>();
		File dir = new File(targetDirectory);
		if(dir.exists()) {
			for(File file : dir.listFiles()) {
				BuildOrderInfo tmpInfo = readBuildOrderFromFile(file.getAbsolutePath());
				if(tmpInfo != null) {
					result.add(tmpInfo);
				}
			}
		}

		return result;
	}

	@Override
	public void saveToStorage(BuildOrderInfo item, String storageFolder, String fileName) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(storageFolder + File.separator + getAdjustedFileName(fileName)));
			new Gson().toJson(item, writer);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				if(writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public SC2VersionInfo readVersionInfoFromFile(String path) {
		SC2VersionInfo info = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
			info = new Gson().fromJson(br, SC2VersionInfo.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return info;
	}

	@Override
	public BuildOrderInfo readBuildOrderFromFile(String path) {
		BuildOrderInfo buildOrder = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			buildOrder = new Gson().fromJson(br, BuildOrderInfo.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return buildOrder;
	}

	@Override
	public void deleteFromStorage(String fileName) {
		File fileToDelete = new File(FileStorageHelper.getBuildOrdersDirectory() + File.separator + fileName);
		if(fileToDelete.exists()) {
			fileToDelete.delete();
		}
	}

	private String getAdjustedFileName(String fileName) {
		return fileName.contains(".txt") ? fileName : fileName + ".txt";
	}
}
