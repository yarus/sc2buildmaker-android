package com.sc2toolslab.sc2bm.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.ui.model.BuildItemHolder;
import com.sc2toolslab.sc2bm.ui.providers.BuildItemImageProvider;
import com.sc2toolslab.sc2bm.ui.utils.UiDataViewHelper;

import java.util.ArrayList;
import java.util.List;

public class BuildItemsListAdapter extends ArrayAdapter<BuildOrderProcessorItem> {
	private BuildItemImageProvider mImageProvider;

	private final Context context;
	private List<BuildOrderProcessorItem> values;
	private int selectedPosition;

	public BuildItemsListAdapter(Context context, List<BuildOrderProcessorItem> data) {
		super(context, R.layout.fragment_build_maker_list_item, data);
		this.context = context;
		this.values = data;
		this.selectedPosition = data.size() - 1;

		mImageProvider = new BuildItemImageProvider();
	}

	public BuildItemsListAdapter(Context context, List<BuildOrderProcessorItem> data, int selectedPosition) {
		this(context, data);
		this.selectedPosition = selectedPosition;
	}

	public void updateData(List<BuildOrderProcessorItem> data, int selectedPosition) {
		//values = data;
		List<BuildOrderProcessorItem> newItems = new ArrayList<>();
		newItems.addAll(data);

		this.selectedPosition = selectedPosition;
		this.clear();
		for(BuildOrderProcessorItem item : newItems) {
			this.add(item);
		}

		notifyDataSetChanged();
	}

	public void setSelectedIndex(Integer index) {
		this.selectedPosition = index;
		notifyDataSetChanged();
	}

	public int getSelectedIndex() {
		return this.selectedPosition;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.fragment_build_maker_list_item, parent, false);

			BuildItemHolder holder = new BuildItemHolder();
			holder.txtSupply = (TextView) rowView.findViewById(R.id.builder_item_supply);
			holder.image = (ImageView) rowView.findViewById(R.id.builder_item_image);
			holder.txtName = (TextView) rowView.findViewById(R.id.builder_item_name);
			holder.txtTimeStart = (TextView) rowView.findViewById(R.id.builder_item_timeStart);
			holder.txtTimeFinish = (TextView) rowView.findViewById(R.id.builder_item_timeFinish);

			rowView.setTag(holder);
		}

		if (this.values.size() == 0) {
			return rowView;
		}

		BuildOrderProcessorItem entry = values.get(position);

		if (entry != null) {
			BuildItemHolder holder = (BuildItemHolder) rowView.getTag();

			holder.txtSupply.setText(String.valueOf(entry.getStatisticsProvider().getCurrentSupply()) + "/"
					+ String.valueOf(entry.getStatisticsProvider().getMaximumSupply()));

			Integer imageId = mImageProvider.getImageResourceIdByKey(entry.getItemName());
			if (imageId != null) {
				holder.image.setImageResource(imageId);
			} else {
				holder.image.setImageResource(R.drawable.empty_cell);
			}

			holder.txtName.setText(entry.getDisplayName());

			BuildOrderProcessorItem nextEntry = null;
			if ((selectedPosition + 1) < values.size()) {
				nextEntry = values.get(selectedPosition + 1);
			}

			_setTimeForEntity(holder, entry, nextEntry);

			View layout = rowView.findViewById(R.id.builder_item_layout);

			if(selectedPosition == position) {
				layout.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
			} else {
				if (position % 2 == 0) {
					layout.setBackgroundColor(Color.parseColor("#333333"));
				} else {
					layout.setBackgroundColor(Color.parseColor("#232323"));
				}
			}
		}

		return rowView;
	}

	private void _setTimeForEntity(BuildItemHolder holder, BuildOrderProcessorItem entry, BuildOrderProcessorItem nextEntry) {
		if (nextEntry == null) {
			nextEntry = values.get(values.size() - 1);
		}

		if(nextEntry.getSecondInTimeLine() > entry.getFinishedSecond()) {
			holder.txtTimeStart.setTextColor(Color.parseColor("#828282"));
			holder.txtTimeFinish.setTextColor(Color.parseColor("#828282"));
		} else {
			holder.txtTimeStart.setTextColor(Color.parseColor("#f5f4f4"));
			holder.txtTimeFinish.setTextColor(Color.parseColor("#f5f4f4"));
		}

		holder.txtTimeStart.setText(UiDataViewHelper.getTimeStringFromSeconds(entry.getSecondInTimeLine()));
		holder.txtTimeFinish.setText(UiDataViewHelper.getTimeStringFromSeconds(entry.getFinishedSecond()));
	}
}
