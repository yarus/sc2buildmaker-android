package com.sc2toolslab.sc2bm.ui.presenters;

import android.os.AsyncTask;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.datacontracts.BuildOrderInfo;
import com.sc2toolslab.sc2bm.datamanagers.InfoEntityConverter;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessor;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.BuildProcessorConfigurationProvider;
import com.sc2toolslab.sc2bm.ui.utils.JsonBuildOrdersResponse;
import com.sc2toolslab.sc2bm.ui.utils.WebApiHelper;
import com.sc2toolslab.sc2bm.ui.views.IOnlineLibraryView;

import java.util.ArrayList;

public class OnlineLibraryPresenter implements IPresenter {
	private IOnlineLibraryView mView;
	private ArrayList<BuildOrderEntity> downloadedBuilds = null;
	private String downloadErrorMessage = "";

	public OnlineLibraryPresenter(IOnlineLibraryView mView) {
		this.mView = mView;

		_loadOnlineBuildOrders();
	}

	private void _loadOnlineBuildOrders() {

		Toast.makeText(mView.getContext(), "Downloading build orders from sc2bm.com...", Toast.LENGTH_SHORT).show();

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids) {
				String version = BuildOrdersProvider.getInstance(mView.getContext()).getVersionFilter();
				RaceEnum faction = BuildOrdersProvider.getInstance(mView.getContext()).getFactionFilter();

				JsonBuildOrdersResponse response = WebApiHelper.getBuildOrders(mView.getContext(), version, faction, "");
				if (response != null && response.Success) {

					ArrayList<BuildOrderEntity> result = _getEntitiesFromInfo(response.Result);

					ArrayList<BuildOrderEntity> uniqueBuilds = _getUniqueBuilds(result);

					if (uniqueBuilds.size() == 0) {
						downloadedBuilds = null;
						downloadErrorMessage = "There are no new build orders in online library";
						return null;
					} else {
						downloadedBuilds = uniqueBuilds;
					}
				} else if(response != null && !response.Message.equals("")) {
					downloadErrorMessage = response.Message;
				} else {
					downloadErrorMessage = "There was an error while downloading build orders from www.sc2bm.com";
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				if (downloadedBuilds != null && downloadedBuilds.size() > 0 && downloadErrorMessage.equals("")) {
					mView.renderList(downloadedBuilds);
				} else {
					Toast.makeText(mView.getContext(), downloadErrorMessage, Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
	}

	private ArrayList<BuildOrderEntity> _getEntitiesFromInfo(ArrayList<BuildOrderInfo> builds)
	{
		ArrayList<BuildOrderEntity> result = new ArrayList<>();

		if (builds == null) {
			return result;
		}

		for (BuildOrderInfo buildOrderInfo : builds) {
			buildOrderInfo.setCreationDate(buildOrderInfo.getAddedDate().getTime());
			BuildOrderEntity entity = InfoEntityConverter.convert(buildOrderInfo);
			result.add(entity);
		}

		return result;
	}

	private ArrayList<BuildOrderEntity> _getUniqueBuilds(ArrayList<BuildOrderEntity> builds) {
		ArrayList<BuildOrderEntity> uniqueBuilds = new ArrayList<>();

		if (builds == null) {
			return uniqueBuilds;
		}

		for(int i = 0;i < builds.size();i++) {
			BuildOrderEntity build = builds.get(i);

			if (BuildOrdersProvider.getInstance(mView.getContext()).getBuildOrderByName(build.getName()) == null) {
				BuildOrderProcessor processor = BuildProcessorConfigurationProvider.getInstance().getProcessorForBuild(build, true);
				BuildOrderProcessorData boData = processor.getCurrentBuildOrder();

				build.setBuildLengthInSeconds(boData.getBuildLengthInSeconds());

				uniqueBuilds.add(build);
			}
		}

		return uniqueBuilds;
	}
}
