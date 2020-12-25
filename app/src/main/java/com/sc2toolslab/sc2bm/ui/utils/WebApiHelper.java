package com.sc2toolslab.sc2bm.ui.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.datacontracts.BuildOrderInfo;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrderEncoder;

import java.util.ArrayList;
import java.util.List;

public class WebApiHelper {
	private WebApiHelper() {}

	public static JsonBuildUploadResponse uploadBuildOrder(Context context, String userName, String password, BuildOrderInfo buildOrder) {
		if (!_hasNetwork(context)) {
			return getUploadResponse(false, "Internet is not available. Build order can't be uploaded.");
		}

		String urlString = getRequestUrl("MobileApi/UploadBuildOrder");

		try {
			int requestLength = getRequestWithoutDescriptionLength(userName, password,buildOrder);

			if (requestLength > AppConstants.MAX_REQUEST_LENGTH) {
				return getUploadResponse(false, "Build is to big to be uploaded from the app. Try to remove description or reduce number of build items. Build can be uploaded directly from sc2bm.com.");
			}

			String description = getDescriptionBasedOnLength(buildOrder, requestLength);

			String body = HttpRequest.post(urlString, true, "userName", userName, "password", password, "name", buildOrder.getName(),
					"versionId", buildOrder.getSC2VersionID(), "race", buildOrder.getRace(), "vsRace", buildOrder.getVsRace(),
					"buildItems", getBuildItemsString(buildOrder.getBuildOrderItems()),
					"description", description).body();

			if (body != null && !body.equals("")) {
				GsonBuilder gsonBuilder = new GsonBuilder();
				Gson gson = gsonBuilder.create();

				JsonBuildUploadResponse jsonResponse = gson.fromJson(body, JsonBuildUploadResponse.class);
				if (jsonResponse != null && jsonResponse.Success) {
					return getUploadResponse(true, "Build order " + buildOrder.getName() + " uploaded to sc2bm.com!");
				} else if (jsonResponse != null && !jsonResponse.Message.equals("")){
					return getUploadResponse(false, jsonResponse.Message);
				}
			}
		} catch(HttpRequest.HttpRequestException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return getUploadResponse(false, "There was an error while uploading build order.");
	}

	public static JsonBuildOrdersResponse getBuildOrders(Context context, String versionId, RaceEnum faction, String name) {
		if (!_hasNetwork(context)) {
			return getBuildsResponse(false, "Internet is not available. Build order can't be uploaded.", null);
		}

		String urlString = getRequestUrl("MobileApi/GetBuilds");

		try {
			String race = faction != null ? faction.toString() : "";

			HttpRequest request = HttpRequest.get(urlString, true, "race", race, "vsRace", "", "versionId", versionId, "name", name);

			String response = request.body();

			if (response != null && !response.equals("")) {
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				Gson gson = gsonBuilder.create();

				JsonBuildOrdersResponse jsonResponse = gson.fromJson(response, JsonBuildOrdersResponse.class);

				if (jsonResponse != null && jsonResponse.Success) {
					return jsonResponse;
				}
			}
		} catch(HttpRequest.HttpRequestException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return getBuildsResponse(false, "There was an error while downloading build orders", null);
	}

	private static boolean _hasNetwork(Context context) {
		boolean hasNetwork = false;

		ConnectivityManager check = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = check.getAllNetworkInfo();

		for (int i = 0; i<info.length; i++){
			if (info[i].getState() == NetworkInfo.State.CONNECTED){
				hasNetwork = true;
				break;
			}
		}

		return hasNetwork;
	}

	private static JsonBuildUploadResponse getUploadResponse(boolean success, String message) {
		JsonBuildUploadResponse response = new JsonBuildUploadResponse();
		response.Success = success;
		response.Message = message;
		return response;
	}

	private static JsonBuildOrdersResponse getBuildsResponse(boolean success, String message, ArrayList<BuildOrderInfo> builds) {
		JsonBuildOrdersResponse response = new JsonBuildOrdersResponse();
		response.Result = builds;
		response.Message = message;
		response.Success = success;
		return response;
	}

	private static int getRequestWithoutDescriptionLength(String userName, String password, BuildOrderInfo buildOrder) {
		return "userName".length() + userName.length() + "password".length() + password.length() +
				"name".length() + buildOrder.getName().length() + "versionId".length() + buildOrder.getSC2VersionID().length() +
				"race".length() + buildOrder.getRace().length() + "vsRace".length() + buildOrder.getVsRace().length() +
				"buildItems".length() + getBuildItemsString(buildOrder.getBuildOrderItems()).length() +
				"description".length();
	}

	private static String getDescriptionBasedOnLength(BuildOrderInfo buildOrder, int requestLength) {
		int availableDescriptionLength = AppConstants.MAX_REQUEST_LENGTH - requestLength;
		String description = "";
		if (availableDescriptionLength > 0) {
			if (availableDescriptionLength >= buildOrder.getDescription().length()) {
				description = buildOrder.getDescription();
			} else {
				description = buildOrder.getDescription().substring(0, availableDescriptionLength);
			}
		}
		return description;
	}

	private static String getBuildItemsString(List<String> items) {
		String result = "";

		BuildOrderEncoder enc = new BuildOrderEncoder();

		for (int i = 0; i < items.size(); i++) {
			if (!result.equals("")) {
				result += ",";
			}

			result += enc.getCode(items.get(i));
		}

		return result;
	}

	private static String getRequestUrl(String apiMethod) {
		return AppConstants.WEB_API_ADDRESS + apiMethod;
	}
}