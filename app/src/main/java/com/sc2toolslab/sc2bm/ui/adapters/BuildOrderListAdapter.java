package com.sc2toolslab.sc2bm.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.model.BuildOrderHolder;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.FactionImageProvider;
import com.sc2toolslab.sc2bm.ui.providers.IImageProvider;
import com.sc2toolslab.sc2bm.ui.utils.UiDataViewHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BuildOrderListAdapter extends ArrayAdapter<BuildOrderEntity> {
	Context mContext;
	private ArrayList<BuildOrderEntity> mData;
	private IImageProvider<RaceEnum> mFactionImageProvider;

	public void updateData(ArrayList<BuildOrderEntity> data) {
		clear();

		addBuildsToList(data);
	}

	public BuildOrderListAdapter(Context context, ArrayList<BuildOrderEntity> data) {
		super(context, R.layout.fragment_online_lib_item);
		this.mContext = context;
		this.mFactionImageProvider = new FactionImageProvider();

		addBuildsToList(data);
	}

	private void addBuildsToList(ArrayList<BuildOrderEntity> data) {
		this.mData = data;

		addAll(data);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;

		BuildOrderHolder holder;

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(R.layout.fragment_online_lib_item, parent, false);
			//row.setId(position);

			holder = new BuildOrderHolder();
			holder.layoutItem = (LinearLayout) row.findViewById(R.id.layoutItem);
			holder.txtBuildName = (TextView) row.findViewById(R.id.txtBuildName);
			holder.imgFaction = (ImageView) row.findViewById(R.id.imgFaction);
			holder.imgVsFaction = (ImageView) row.findViewById(R.id.imgVsFaction);
			holder.txtLength = (TextView) row.findViewById(R.id.txtLength);
			holder.txtDate = (TextView) row.findViewById(R.id.txtDate);
			holder.imgDownload = (ImageView) row.findViewById(R.id.imgDownload);

			row.setTag(holder);
		} else {
			holder = (BuildOrderHolder) row.getTag();
		}

		_setDownloadClickListener(holder.imgDownload, position);

		_bindDataToHolder(holder, mData.get(position));

		return row;
	}

	private void _setDownloadClickListener(ImageView imgDownload, final int position) {
		imgDownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mData.size() == 0) {
					return;
				}

				BuildOrderEntity build = mData.get(position);

				BuildOrdersProvider.getInstance(getContext()).saveBuildOrder(build);

				// Build is saved so there is no need to show it
				mData.remove(build);

				updateData(mData);

				Toast.makeText(v.getContext(), build.getName() + " downloaded!", Toast.LENGTH_LONG).show();
			}
		});
	}

	private void _bindDataToHolder(BuildOrderHolder holder, BuildOrderEntity dataItem) {
		String matchup = UiDataViewHelper.getFactionLetter(dataItem.getRace()) + "v" + UiDataViewHelper.getFactionLetter(dataItem.getVsRace());

		holder.txtBuildName.setText(matchup + " " + dataItem.getName());

		if (dataItem.getVsRace() == RaceEnum.Terran) {
			holder.layoutItem.setBackgroundResource(R.color.terranBuild);
		} else if (dataItem.getVsRace() == RaceEnum.Protoss) {
			holder.layoutItem.setBackgroundResource(R.color.protossBuild);
		} else if (dataItem.getVsRace() == RaceEnum.Zerg) {
			holder.layoutItem.setBackgroundResource(R.color.zergBuild);
		} else {
			holder.layoutItem.setBackgroundResource(R.color.randomBuild);
		}

		holder.imgFaction.setImageResource(mFactionImageProvider.getImageResourceIdByKey(dataItem.getRace()));
		holder.imgVsFaction.setImageResource(mFactionImageProvider.getImageResourceIdByKey(dataItem.getVsRace()));

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.US);

		holder.txtDate.setText(simpleDateFormat.format(new Date(dataItem.getCreationDate())));
		holder.txtLength.setText("Length: " + UiDataViewHelper.getTimeStringFromSeconds(dataItem.getBuildLengthInSeconds()));
	}
}
