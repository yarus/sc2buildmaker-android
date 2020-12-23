package com.sc2toolslab.sc2bm.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.activities.MainActivity;
import com.sc2toolslab.sc2bm.ui.model.BuildOrderListItemHolder;
import com.sc2toolslab.sc2bm.ui.model.BuildOrderViewModel;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.FactionImageProvider;
import com.sc2toolslab.sc2bm.ui.providers.IImageProvider;
import com.sc2toolslab.sc2bm.ui.utils.NavigationManager;
import com.sc2toolslab.sc2bm.ui.utils.UiDataViewHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class BuildListGridViewAdapter extends ArrayAdapter<BuildOrderEntity> {
	Context mContext;
	int mLayoutResourceId;
	private IImageProvider<RaceEnum> mFactionImageProvider;
	private ArrayList<BuildOrderViewModel> mData;

	private int mItemHeight = 0;
	private int mNumColumns = 0;

	private boolean mShowDefaultBuilds = true;

	public void updateData(ArrayList<BuildOrderEntity> data) {
		clear();

		addBuildsToList(data);
	}

	public BuildListGridViewAdapter(Context context, int layoutResourceId, ArrayList<BuildOrderEntity> data) {
		super(context, layoutResourceId);
		this.mContext = context;
		this.mLayoutResourceId = layoutResourceId;
		this.mFactionImageProvider = new FactionImageProvider();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.mShowDefaultBuilds = prefs.getBoolean("ShowDefaultBuilds", true);

		addBuildsToList(data);
	}

	private void addBuildsToList(ArrayList<BuildOrderEntity> data) {
		ArrayList<BuildOrderEntity> filteredBuilds = new ArrayList<>();

		BuildOrdersProvider provider = BuildOrdersProvider.getInstance(mContext);
		for(BuildOrderEntity buildOrder : data) {
			if (mShowDefaultBuilds || (!mShowDefaultBuilds && !provider.isBuildDefault(buildOrder.getName()))) {
				filteredBuilds.add(buildOrder);
			}
		}

		this.mData = _getBuildOrdersViewModel(filteredBuilds);

		addAll(filteredBuilds);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;

		BuildOrderListItemHolder holder;

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
			//row.setId(position);

			holder = new BuildOrderListItemHolder();
			holder.layoutItem = (LinearLayout) row.findViewById(R.id.layoutItem);
			holder.txtBuildName = (TextView) row.findViewById(R.id.txtBuildName);
			holder.imgFaction = (ImageView) row.findViewById(R.id.imgFaction);
			holder.imgVsFaction = (ImageView) row.findViewById(R.id.imgVsFaction);
			holder.txtLength = (TextView) row.findViewById(R.id.txtLength);
			holder.txtDate = (TextView) row.findViewById(R.id.txtDate);
			holder.imgFavIcon = (ImageView) row.findViewById(R.id.imgFav);
			holder.imgBuildMaker = (ImageView) row.findViewById(R.id.imgEdit);
			holder.imgBuildPlayer = (ImageView) row.findViewById(R.id.imgPlay);
			holder.imgSimulate = (ImageView) row.findViewById(R.id.imgSimulate);
			row.setTag(holder);
		} else {
			holder = (BuildOrderListItemHolder) row.getTag();
		}

		_setFavIconClickListener(holder.imgFavIcon, position);
		_setEditClickListener(holder.imgBuildMaker, position);
		_setPlayClickListener(holder.imgBuildPlayer, position);
		_setSimulateClickListener(holder.imgSimulate, position);

		_bindDataToHolder(holder, mData.get(position));

		return row;
	}

	private void _setPlayClickListener(ImageView imgBuildPlayer, final int position) {
		imgBuildPlayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BuildOrderViewModel build = mData.get(position);

				NavigationManager.startBuildPlayerActivity((Activity) getContext(), build.Data.getName());
			}
		});
		imgBuildPlayer.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(v.getContext(), "Open build player", Toast.LENGTH_LONG).show();
				return true;
			}
		});
	}

	private void _setSimulateClickListener(ImageView imgSimulate, final int position) {
		imgSimulate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BuildOrderViewModel build = mData.get(position);
				NavigationManager.startSimulatorResultsActivity((Activity) getContext(), build.Data.getName());
			}
		});
		imgSimulate.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(v.getContext(), "Open Build Results", Toast.LENGTH_LONG).show();
				return true;
			}
		});
	}

	private void _setFavIconClickListener(ImageView imgFavIcon, final int position) {
		imgFavIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BuildOrderViewModel build = mData.get(position);
				build.IsFavorite = !build.IsFavorite;

				if (build.IsFavorite) {
					BuildOrdersProvider.getInstance(getContext()).addFavoriteBuild(build.Data.getName());
				} else {
					BuildOrdersProvider.getInstance(getContext()).removeFavoriteBuild(build.Data.getName());
				}

				_saveFavoriteBuilds();

				notifyDataSetChanged();
			}
		});
		imgFavIcon.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(v.getContext(), "Mark build as Favorite", Toast.LENGTH_LONG).show();

				return true;
			}
		});
	}

	private void _setEditClickListener(ImageView imgEdit, final int position) {
		imgEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BuildOrderViewModel build = mData.get(position);

				NavigationManager.startBuildMakerActivity((Activity)getContext(), build.Data.getName());
			}
		});
		imgEdit.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(v.getContext(), "Edit build order", Toast.LENGTH_LONG).show();
				return true;
			}
		});
	}

	private void _saveFavoriteBuilds() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = settings.edit();

		HashSet<String> result = new HashSet<>();

		for(String build : BuildOrdersProvider.getInstance(getContext()).getFavoriteBuildOrders()) {
			result.add(build);
		}

		editor.putStringSet(mContext.getString(R.string.pref_FavoriteBuilds), result);
		editor.apply();
	}

	private void _bindDataToHolder(BuildOrderListItemHolder holder, BuildOrderViewModel dataItem) {
		String matchup = UiDataViewHelper.getFactionLetter(dataItem.Data.getRace()) + "v" + UiDataViewHelper.getFactionLetter(dataItem.Data.getVsRace());

		holder.txtBuildName.setText(matchup + " " + dataItem.Data.getName());

		if (dataItem.Data.getVsRace() == RaceEnum.Terran) {
			holder.layoutItem.setBackgroundResource(R.color.terranBuild);
		} else if (dataItem.Data.getVsRace() == RaceEnum.Protoss) {
			holder.layoutItem.setBackgroundResource(R.color.protossBuild);
		} else if (dataItem.Data.getVsRace() == RaceEnum.Zerg) {
			holder.layoutItem.setBackgroundResource(R.color.zergBuild);
		} else {
			holder.layoutItem.setBackgroundResource(R.color.randomBuild);
		}

		holder.imgFaction.setImageResource(mFactionImageProvider.getImageResourceIdByKey(dataItem.Data.getRace()));
		holder.imgVsFaction.setImageResource(mFactionImageProvider.getImageResourceIdByKey(dataItem.Data.getVsRace()));

		if (dataItem.IsFavorite) {
			holder.imgFavIcon.setImageResource(R.mipmap.ic_favorite_yes);
		} else {
			holder.imgFavIcon.setImageResource(R.mipmap.ic_favorite_no);
		}

		holder.txtDate.setText(dataItem.VisitedDateString);
		holder.txtLength.setText("Length: " + dataItem.LengthString);
	}

	// set numcols
	public void setNumColumns(int numColumns) {
		mNumColumns = numColumns;
	}

	public int getNumColumns() {
		return mNumColumns;
	}

	// set photo item height
	public void setItemHeight(int height) {
		if (height == mItemHeight) {
			return;
		}
		mItemHeight = height;
		notifyDataSetChanged();
	}

	private ArrayList<BuildOrderViewModel> _getBuildOrdersViewModel(ArrayList<BuildOrderEntity> builds) {
		ArrayList<BuildOrderViewModel> result = new ArrayList<>();

		for (BuildOrderEntity build : builds) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.US);

			BuildOrderViewModel item = new BuildOrderViewModel();
			item.Data = build;
			item.IsFavorite = _isBuildFavorite(build.getName());
			item.IsLatestVersion = true;
			item.LengthString = UiDataViewHelper.getTimeStringFromSeconds(build.getBuildLengthInSeconds());
			item.CreatedDateString = simpleDateFormat.format(new Date(build.getCreationDate()));
			item.VisitedDateString = simpleDateFormat.format(new Date(build.getVisitedDate()));
			item.IsLatestVersion = _isLatestVersion(build);
			result.add(item);
		}

		return result;
	}

	private Boolean _isBuildFavorite(String buildName) {
		return BuildOrdersProvider.getInstance(getContext()).isBuildFavorite(buildName);
	}

	private Boolean _isLatestVersion(BuildOrderEntity entity) {
		String version = entity.getsC2VersionID();

		return version.equals(AppConstants.WOL_Config) || version.equals(AppConstants.HOTS_Config) || version.equals(AppConstants.LOTV_Config);
	}
}
