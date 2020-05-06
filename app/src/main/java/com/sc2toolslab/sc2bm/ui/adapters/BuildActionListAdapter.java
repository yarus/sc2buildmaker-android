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
import com.sc2toolslab.sc2bm.ui.model.BuildActionHolder;
import com.sc2toolslab.sc2bm.ui.providers.BuildItemImageProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BuildActionListAdapter extends ArrayAdapter<BuildOrderProcessorItem> {
    private BuildItemImageProvider mImageProvider;

    private final Context context;
    private List<BuildOrderProcessorItem> values;
    private List<BuildOrderProcessorItem> shownValues;
    private int selectedIndex;
    private BuildOrderProcessorItem selectedItem;

    public BuildActionListAdapter(Context context, List<BuildOrderProcessorItem> data, int selectedIndex) {
        super(context, R.layout.fragment_build_maker_action_item, data);

        this.context = context;
        this.values = data;
        this.shownValues = new ArrayList<>();
        this.selectedIndex = selectedIndex;

        mImageProvider = new BuildItemImageProvider();
    }

    public void updateData(List<BuildOrderProcessorItem> data, int selectedIndex) {
        this.values = new ArrayList<>(data);

        this.selectedIndex = selectedIndex;

        if (this.values.size() > 0 && this.values.size() > selectedIndex) {
            this.selectedItem = data.get(selectedIndex);
        }

        this.shownValues = new ArrayList<>();

        if (this.selectedItem == null) {
            return;
        }

        for(BuildOrderProcessorItem item : data) {
            if (item.getFinishedSecond() > this.selectedItem.getSecondInTimeLine()) {
                this.shownValues.add(item);
            }
        }

        Collections.sort(this.shownValues, new Comparator<BuildOrderProcessorItem>() {
            public int compare(BuildOrderProcessorItem item1, BuildOrderProcessorItem item2) {
                return item1.getFinishedSecond().compareTo(item2.getFinishedSecond());
            }
        });

        this.clear();

        for(BuildOrderProcessorItem item : this.shownValues) {
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

            rowView.setTag(holder);
        }

        if (this.shownValues.size() == 0) {
            return rowView;
        }

        BuildOrderProcessorItem entry = this.shownValues.get(position);

        if (entry != null) {
            BuildActionHolder holder = (BuildActionHolder) rowView.getTag();

            Integer imageId = mImageProvider.getImageResourceIdByKey(entry.getItemName());
            if (imageId != null) {
                holder.imgIcon.setImageResource(imageId);
            } else {
                holder.imgIcon.setImageResource(R.drawable.empty_cell);
            }

            BuildOrderProcessorItem selectedItem = this.values.get(this.selectedIndex);

            holder.txtTimeLeft.setText("~" + (entry.getFinishedSecond() - selectedItem.getSecondInTimeLine()) + " s");
        }

        return rowView;
    }
}
