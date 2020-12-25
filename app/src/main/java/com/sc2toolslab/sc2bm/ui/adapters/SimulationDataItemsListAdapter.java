package com.sc2toolslab.sc2bm.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.model.AddBuildItemHolder;
import com.sc2toolslab.sc2bm.ui.model.SimulatorDataItem;
import com.sc2toolslab.sc2bm.ui.presenters.SimulatorModeEnum;
import com.sc2toolslab.sc2bm.ui.providers.BuildItemImageProvider;

import java.util.ArrayList;
import java.util.List;

public class SimulationDataItemsListAdapter extends ArrayAdapter<SimulatorDataItem> {
    private final Context mContext;
    private List<SimulatorDataItem> mBuildItems;
    private final BuildItemImageProvider mImageProvider;

    private int mItemHeight = 0;
    private int mNumColumns = 0;

    public SimulationDataItemsListAdapter(Context context, List<SimulatorDataItem> buildItems) {
        super(context, R.layout.fragment_build_maker_add_item, buildItems);

        this.mContext = context;
        this.mBuildItems = buildItems;

        this.mImageProvider = new BuildItemImageProvider();
    }

    public void updateData(List<SimulatorDataItem> buildItems) {
        //values = data;
        List<SimulatorDataItem> newItems = new ArrayList<>(buildItems);

        this.clear();

        for(SimulatorDataItem item : newItems) {
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
            holder.imgItemStatus = (ImageView) rowView.findViewById(R.id.imgItemStatus);

            rowView.setTag(holder);
        }

        SimulatorDataItem entry = mBuildItems.get(position);

        if (entry != null) {
            AddBuildItemHolder holder = (AddBuildItemHolder) rowView.getTag();

            Integer imageId = mImageProvider.getImageResourceIdByKey(entry.Name);
            if (imageId != null) {
                holder.imgItemImage.setImageResource(imageId);
            } else {
                holder.imgItemImage.setImageResource(R.drawable.empty_cell);
            }

            String itemName = entry.DisplayName;

            holder.txtItemName.setText(itemName);

            if (entry.Count > 1) {
                holder.txtItemCount.setText(Integer.toString(entry.Count));
            } else {
                holder.txtItemCount.setText("");
            }

            if (entry.IsEnabled && entry.IsClickable && entry.Mode == SimulatorModeEnum.BASE && entry.Type == BuildItemTypeEnum.Building) {
                _setStatusImageForRace(entry.Race, holder.imgItemStatus);
                holder.imgItemStatus.setVisibility(View.VISIBLE);
            } else if (entry.IsEnabled && !entry.IsClickable && entry.Type == BuildItemTypeEnum.Upgrade) {
                holder.imgItemStatus.setImageResource(R.drawable.check_white);
                holder.imgItemStatus.setVisibility(View.VISIBLE);
            } else {
                holder.imgItemStatus.setVisibility(View.INVISIBLE);
            }

            if (!entry.IsEnabled) {
                holder.imgGrayed.setBackgroundResource(R.drawable.grayed);
                holder.imgGrayed.setVisibility(View.VISIBLE);
            } else {
                holder.imgGrayed.setBackgroundResource(R.color.transparent);
                holder.imgGrayed.setVisibility(View.INVISIBLE);
            }
        }

        return rowView;
    }

    private void _setStatusImageForRace(RaceEnum race, ImageView img) {
        if (race == RaceEnum.Terran) {
            img.setImageResource(R.drawable.build_structure_terran);
        } else if (race == RaceEnum.Protoss) {
            img.setImageResource(R.drawable.build_structure_protoss);
        } else if (race == RaceEnum.Zerg) {
            img.setImageResource(R.drawable.build_structure_zerg);
        }
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
