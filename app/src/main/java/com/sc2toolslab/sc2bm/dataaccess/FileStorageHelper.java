package com.sc2toolslab.sc2bm.dataaccess;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import com.sc2toolslab.sc2bm.constants.AppConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileStorageHelper {
	private static List<String> defaultBuildOrdersNames = new ArrayList<String>();

	public static boolean checkDirectories(Context context) {
		if (checkExternalStorageAvailable()) {
			try{
				createDirsIfTheyNotExists();

				defaultBuildOrdersNames.clear();
				defaultBuildOrdersNames.addAll(Arrays.asList(context.getAssets().list(AppConstants.DEFAULT_BUILD_ORDERS_ASSETS_FOLDER)));

				updateVersionFiles(context);

				updateDefaultBuildOrders(context);

				return true;

			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		return false;
	}

	private static void updateVersionFiles(Context context) {
		List<String> defaultVersionFileNames = new ArrayList<>();

		AssetManager mgr = context.getAssets();

		try {
			defaultVersionFileNames.addAll(Arrays.asList(mgr.list("")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(String versionFileName : defaultVersionFileNames) {
			if (!versionFileName.contains(".txt")) {
				continue;
			}

			InputStream in ;
			OutputStream out;

			try {
				in = mgr.open(versionFileName);
				out = new FileOutputStream(getBuildConfigurationsDirectory() + File.separator + versionFileName);

				copyFile(in, out);
				in.close();
				out.flush();
				out.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void updateDefaultBuildOrders(Context context) {
		AssetManager mgr = context.getAssets();

		List<String> deletedFilesNames = getDeletedFileNames();

		for(String buildFileName : defaultBuildOrdersNames) {
			if (!buildFileName.contains(".txt")) {
				continue;
			}

			InputStream in ;
			OutputStream out;

			if (fileWasDeleted(buildFileName, deletedFilesNames)) {
				continue;
			}

			try {
				in = mgr.open(AppConstants.DEFAULT_BUILD_ORDERS_ASSETS_FOLDER + File.separator + buildFileName);
				out = new FileOutputStream(getBuildOrdersDirectory() + File.separator + buildFileName);

				copyFile(in, out);
				in.close();
				out.flush();
				out.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void createDirsIfTheyNotExists() {
		File versionsDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator
				+ AppConstants.ROOT_FOLDER_NAME + File.separator + AppConstants.VERSIONS_FOLDER_NAME);
		File buildOrdersDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator
				+ AppConstants.ROOT_FOLDER_NAME + File.separator + AppConstants.BUILD_ORDERS_FOLDER_NAME);

		if(!buildOrdersDir.exists()) {
			buildOrdersDir.mkdirs();
		}
		if(!versionsDir.exists()) {
			versionsDir.mkdirs();
		}
	}

	public static String getBuildOrdersDirectory() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
				+ AppConstants.ROOT_FOLDER_NAME + File.separator + AppConstants.BUILD_ORDERS_FOLDER_NAME;
	}

	public static String getBuildConfigurationsDirectory() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
				+ AppConstants.ROOT_FOLDER_NAME + File.separator + AppConstants.VERSIONS_FOLDER_NAME;
	}

	public static boolean checkBuildOrderIsDefault(String fileName) {
		for(String defaultBuildOrderName : defaultBuildOrdersNames) {
			if(defaultBuildOrderName.equals("" + fileName)) {
				return true;
			}
		}

		return false;
	}

	public static void addFileNameToDeletedList(String fileName) {
		try {
			FileWriter fw = new FileWriter(getRootDirectory() + File.separator + AppConstants.DELETED_FILES_CONFIG_NAME,true);
			fw.write(fileName + "\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//region private methods

	private static boolean checkExternalStorageAvailable() {
		try {
			String state = Environment.getExternalStorageState();

			if (Environment.MEDIA_MOUNTED.equals(state)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}

		return false;
	}

	private static String getRootDirectory() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
				+ AppConstants.ROOT_FOLDER_NAME;
	}

	private static List<String> getDeletedFileNames() {
		List<String> fileNames = new ArrayList<String>();
		try {
			InputStream fis = new FileInputStream(getRootDirectory() + File.separator + AppConstants.DELETED_FILES_CONFIG_NAME);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
			String fileName;
			while ((fileName = br.readLine()) != null) {
				fileNames.add(fileName);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileNames;
	}

	private static boolean fileWasDeleted(String fileName, List<String> listToCompare) {
		for(String deletedFileName : listToCompare) {
			if(fileName.equals(deletedFileName)) {
				return true;
			}
		}

		return false;
	}

	private static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}

	//endregion
}
