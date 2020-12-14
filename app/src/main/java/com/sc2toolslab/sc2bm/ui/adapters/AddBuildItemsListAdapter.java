package com.sc2toolslab.sc2bm.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.ui.model.AddBuildItemHolder;
import com.sc2toolslab.sc2bm.ui.model.AddItemDataItem;
import com.sc2toolslab.sc2bm.ui.providers.BuildItemImageProvider;

import java.util.ArrayList;
import java.util.List;

public class AddBuildItemsListAdapter extends ArrayAdapter<AddItemDataItem> {
	private final Context mContext;
	private List<AddItemDataItem> mBuildItems;
	private final BuildItemImageProvider mImageProvider;

	private int mItemHeight = 0;
	private int mNumColumns = 0;

	public AddBuildItemsListAdapter(Context context, List<AddItemDataItem> data) {
		super(context, R.layout.fragment_build_maker_add_item, data);
		this.mContext = context;
		this.mBuildItems = data;

		this.mImageProvider = new BuildItemImageProvider();
	}

	public void updateData(List<AddItemDataItem> buildItems) {
		//values = data;
		List<AddItemDataItem> newItems = new ArrayList<>(buildItems);

		this.clear();

		for(AddItemDataItem item : newItems) {
			this.add(item);
		}

		this.mBuildItems = buildItems;

		notifyDataSetChanged();
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

		AddItemDataItem entry = mBuildItems.get(position);

		if (entry != null) {
			AddBuildItemHolder holder = (AddBuildItemHolder) rowView.getTag();

			Integer imageId = mImageProvider.getImageResourceIdByKey(entry.Item.getName());
			if (imageId != null) {
				holder.imgItemImage.setImageResource(imageId);
			} else {
				holder.imgItemImage.setImageResource(R.drawable.empty_cell);
			}

			String itemName = entry.Item.getDisplayName();

			if (entry.Count > 0) {
				itemName = itemName + " (" + entry.Count + ")";
			}

			holder.txtItemName.setText(itemName);

			if (!entry.IsOrderAvailable) {
				holder.imgGrayed.setBackgroundResource(R.drawable.grayed);
				holder.txtItemCount.setVisibility(View.INVISIBLE);
			} else {
				holder.txtItemCount.setText(String.format("~%ds", entry.NeededSeconds));
				holder.txtItemCount.setVisibility(View.VISIBLE);
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
