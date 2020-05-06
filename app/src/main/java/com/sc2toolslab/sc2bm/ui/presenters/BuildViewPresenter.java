package com.sc2toolslab.sc2bm.ui.presenters;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.datacontracts.BuildOrderInfo;
import com.sc2toolslab.sc2bm.datamanagers.InfoEntityConverter;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.utils.JsonBuildUploadResponse;
import com.sc2toolslab.sc2bm.ui.utils.UiDataViewHelper;
import com.sc2toolslab.sc2bm.ui.utils.WebApiHelper;
import com.sc2toolslab.sc2bm.ui.views.IBuildView;

import java.util.Date;

public class BuildViewPresenter implements IPresenter {
	private IBuildView mView;
	private BuildOrderEntity mBuildOrder;

	public BuildViewPresenter(IBuildView view, String buildName) {
		this.mView = view;
		this.mBuildOrder = BuildOrdersProvider.getInstance(mView.getContext()).getBuildOrderByName(buildName);

		_updateVisitedDate();

		_bindData();
	}

	public void deleteCurrentBuild() {
		BuildOrdersProvider.getInstance(mView.getContext()).deleteBuildOrder(mBuildOrder.getName());
	}

	public String getBuildName() {
		return mBuildOrder.getName();
	}

	public void updateBuildName(String buildName) {
		mBuildOrder = BuildOrdersProvider.getInstance(mView.getContext()).getBuildOrderByName(buildName);
		_bindData();
	}

	public boolean isBuildDefault() {
		return BuildOrdersProvider.getInstance(mView.getContext()).isBuildDefault(mBuildOrder.getName());
	}

	private JsonBuildUploadResponse uploadResponse = null;
	public void uploadBuildOrder(final String userName, final String password) {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... voids) {

					BuildOrderInfo buildInfo = InfoEntityConverter.convert(mBuildOrder);
					buildInfo.setAddedDate(new Date(buildInfo.getVisitedDate()));

					uploadResponse = WebApiHelper.uploadBuildOrder(mView.getContext(), userName, password, buildInfo);

					return null;
				}

				@Override
				protected void onPostExecute(Void aVoid) {
					if (uploadResponse != null) {
						Toast.makeText(mView.getContext(), uploadResponse.Message, Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(mView.getContext(), "There was an error while uploading build order", Toast.LENGTH_LONG).show();
					}
				}
			}.execute();
	}

	private void _bindData() {
		mView.setBuildName(mBuildOrder.getName());
		mView.setVersion(mBuildOrder.getsC2VersionID());
		mView.setCreated(mBuildOrder.getCreationDate());
		mView.setVisited(mBuildOrder.getVisitedDate());
		mView.setLength(UiDataViewHelper.getTimeStringFromSeconds(mBuildOrder.getBuildLengthInSeconds()));
		mView.setFaction(mBuildOrder.getRace());
		mView.setMatchup(mBuildOrder.getRace(), mBuildOrder.getVsRace());
		mView.setDescription(mBuildOrder.getDescription());
	}

	private void _updateVisitedDate() {
		mBuildOrder.setVisitedDate(System.currentTimeMillis());
		BuildOrdersProvider.getInstance(mView.getContext()).saveBuildOrder(mBuildOrder);
	}
}