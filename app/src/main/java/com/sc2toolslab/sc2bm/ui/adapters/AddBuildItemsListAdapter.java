package com.sc2toolslab.sc2bm.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessor;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorData;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;
import com.sc2toolslab.sc2bm.ui.model.AddBuildItemHolder;
import com.sc2toolslab.sc2bm.ui.providers.BuildItemImageProvider;
import com.sc2toolslab.sc2bm.ui.utils.UiDataViewHelper;

import java.util.List;

public class AddBuildItemsListAdapter extends ArrayAdapter<BuildItemEntity> {
	private Context mContext;
	private List<BuildItemEntity> mBuildItems;
	private BuildItemImageProvider mImageProvider;
	private BuildItemStatistics mLastItemStats;
	private BuildItemStatistics mCurrentStats;
	private BuildOrderProcessor mBuildProcessor;
	private BuildOrderProcessorData mBuildData;
	private BuildOrderProcessorItem mLastBuildItem;

	private int mItemHeight = 0;
	private int mNumColumns = 0;

	public AddBuildItemsListAdapter(Context context, BuildOrderProcessor buildProcessor, List<BuildItemEntity> data, BuildItemStatistics currentStats, BuildItemStatistics lastItemStats) {
		super(context, R.layout.fragment_build_maker_add_item, data);
		this.mContext = context;
		this.mBuildItems = data;
		this.mCurrentStats = currentStats;
		this.mLastItemStats = lastItemStats;
		this.mBuildProcessor = buildProcessor;
		this.mBuildData = mBuildProcessor.getCurrentBuildOrder();
		this.mLastBuildItem = mBuildData.getLastBuildItem();

		//addAll(data);

		this.mImageProvider = new BuildItemImageProvider();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.fragment_build_maker_add_item, parent, false);

			AddBuildItemHolder holder = new AddBuildItemHolder();
			holder.txtItemCount = (TextView) rowView.findViewById(R.id.txtItemCount);
			holder.txtItemName = (TextView) rowView.findViewById(R.id.txtItemName);
			holder.imgItemImage = (ImageView) rowView.findViewById(R.id.imgItemImage);
			holder.imgGrayed = (ImageView) rowView.findViewById(R.id.imgGrayed);

			rowView.setTag(holder);
		}

		BuildItemEntity entry = mBuildItems.get(position);

		if (entry != null) {
			AddBuildItemHolder holder = (AddBuildItemHolder) rowView.getTag();

			Integer imageId = mImageProvider.getImageResourceIdByKey(entry.getName());
			if (imageId != null) {
				holder.imgItemImage.setImageResource(imageId);
			} else {
				holder.imgItemImage.setImageResource(R.drawable.empty_cell);
			}

			boolean satisfied = true;
			for (IBuildItemRequirement requirement : entry.getOrderRequirements()) {
				if (!requirement.isRequirementSatisfied(mLastItemStats)) {
					satisfied = false;
					break;
				}
			}

			String itemName = entry.getDisplayName();

			int statValue = mCurrentStats.getStatValueByName(entry.getName());
			if (statValue > 0) {
				itemName = itemName + " (" + String.valueOf(statValue) + ")";
			}

			holder.txtItemName.setText(itemName);

			if (!satisfied) {
				holder.imgGrayed.setBackgroundResource(R.drawable.grayed);
				holder.txtItemCount.setVisibility(View.INVISIBLE);
			} else {
				if (mBuildProcessor.addBuildItem(entry.getName())) {
					BuildOrderProcessorItem tmpLastItem = mBuildProcessor.getCurrentBuildOrder().getLastBuildItem();

					int needSeconds = tmpLastItem.getSecondInTimeLine() - mLastBuildItem.getSecondInTimeLine();

					holder.txtItemCount.setText("~" + needSeconds + "s");

					mBuildProcessor.undoLastBuildItem();
					holder.txtItemCount.setVisibility(View.VISIBLE);
				}
			}
		}

		return rowView;
	}

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
}
