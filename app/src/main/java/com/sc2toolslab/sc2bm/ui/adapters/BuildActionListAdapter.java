package com.sc2toolslab.sc2bm.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.ui.model.BuildActionHolder;
import com.sc2toolslab.sc2bm.ui.model.QueueDataItem;
import com.sc2toolslab.sc2bm.ui.providers.BuildItemImageProvider;

import java.util.ArrayList;
import java.util.List;

public class BuildActionListAdapter extends ArrayAdapter<QueueDataItem> {
    private final BuildItemImageProvider mImageProvider;

    private final Context context;
    private List<QueueDataItem> shownValues;
    // private int selectedIndex;
    private BuildOrderProcessorItem selectedItem;

    public BuildActionListAdapter(Context context, List<QueueDataItem> data, BuildOrderProcessorItem selectedItem) {
        super(context, R.layout.fragment_build_maker_action_item, data);

        this.context = context;
        this.shownValues = new ArrayList<>();
        this.selectedItem = selectedItem;

        mImageProvider = new BuildItemImageProvider();
    }

    public void updateData(List<QueueDataItem> data, BuildOrderProcessorItem selectedItem) {
        this.selectedItem = selectedItem;

        List<BuildOrderProcessorItem> results = new ArrayList<>();

        if (this.selectedItem == null) {
            return;
        }

        this.shownValues = data;

        this.clear();

        for(QueueDataItem item : this.shownValues) {
            this.add(item);
        }

        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.fragment_build_maker_action_item, parent, false);

            BuildActionHolder holder = new BuildActionHolder();
            holder.txtTimeLeft = (TextView) rowView.findViewById(R.id.txtTimeLeft);
            holder.imgIcon = (ImageView) rowView.findViewById(R.id.imgItem);
            holder.itemFrame = (FrameLayout) rowView.findViewById(R.id.itemFrame);
            holder.txtItemCount = (TextView) rowView.findViewById(R.id.txtItemCount);

            rowView.setTag(holder);
        }

        if (this.shownValues.size() == 0) {
            return rowView;
        }

        QueueDataItem entry = this.shownValues.get(position);

        if (entry != null) {
            BuildActionHolder holder = (BuildActionHolder) rowView.getTag();

            holder.itemFrame.setVisibility(View.VISIBLE);

            Integer imageId = mImageProvider.getImageResourceIdByKey(entry.Item.getItemName());
            if (imageId != null) {
                holder.imgIcon.setImageResource(imageId);
            } else {
                holder.imgIcon.setImageResource(R.drawable.empty_cell);
            }

            if (entry.Count > 1) {
                holder.txtItemCount.setText(String.format("x%d", entry.Count));
                holder.txtItemCount.setVisibility(View.VISIBLE);
            } else {
                holder.txtItemCount.setVisibility(View.INVISIBLE);
            }

            holder.txtTimeLeft.setText("~" + (entry.Item.getFinishedSecond() - selectedItem.getSecondInTimeLine()) + "s");
        }

        return rowView;
    }
}
