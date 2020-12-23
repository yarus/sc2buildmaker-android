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
import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.domain.BuildItemTypeEnum;
import com.sc2toolslab.sc2bm.engine.EngineConsts;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessor;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.engine.interfaces.IBuildItemRequirement;
import com.sc2toolslab.sc2bm.ui.model.AddBuildItemHolder;
import com.sc2toolslab.sc2bm.ui.providers.BuildItemImageProvider;
import com.sc2toolslab.sc2bm.ui.providers.BuildProcessorConfigurationProvider;

import java.util.ArrayList;
import java.util.List;

public class ProductionListAdapter extends ArrayAdapter<BuildItemEntity> {
    private final Context mContext;
    private List<BuildItemEntity> mBuildItems;
    private BuildItemStatistics mCurrentStats;
    private final BuildItemImageProvider mImageProvider;
    private BuildItemEntity mSelectedItem;

    public ProductionListAdapter(Context context, List<BuildItemEntity> buildItems, BuildItemStatistics currentStats, BuildItemEntity selectedItem) {
        super(context, R.layout.fragment_build_maker_add_item, buildItems);
        this.mContext = context;
        this.mBuildItems = buildItems;
        this.mCurrentStats = currentStats;
        this.mSelectedItem = selectedItem;

        this.mImageProvider = new BuildItemImageProvider();
    }

    public void updateData(List<BuildItemEntity> buildItems, BuildItemStatistics currentStats, BuildItemEntity selectedItem) {
        //values = data;
        List<BuildItemEntity> newItems = new ArrayList<>(buildItems);

        this.clear();

        for(BuildItemEntity item : newItems) {
            this.add(item);
        }

        this.mBuildItems = buildItems;
        this.mCurrentStats = currentStats;
        this.mSelectedItem = selectedItem;

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

        BuildItemEntity entry = mBuildItems.get(position);

        if (entry != null) {
            AddBuildItemHolder holder = (AddBuildItemHolder) rowView.getTag();

            Integer imageId = mImageProvider.getImageResourceIdByKey(entry.getName());
            if (imageId != null) {
                holder.imgItemImage.setImageResource(imageId);
            } else {
                holder.imgItemImage.setImageResource(R.drawable.empty_cell);
            }

            String itemName = entry.getDisplayName();

            int statValue = mCurrentStats.getStatValueByName(entry.getName());
            int buzyValue = mCurrentStats.getStatValueByName(entry.getName() + EngineConsts.BUZY_BUILD_ITEM_POSTFIX);
            int freeValue = statValue - buzyValue;

            holder.txtItemName.setText(itemName);

            View layout = rowView.findViewById(R.id.itemLayout);
            if (mSelectedItem != null && entry.getName().equals(mSelectedItem.getName())) {
                layout.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
            } else {
                layout.setBackgroundColor(Color.parseColor("#333333"));
            }

            if (freeValue > 0) {
                holder.txtItemCount.setText(Integer.toString(freeValue));
            } else {
                holder.txtItemCount.setText("");
            }
        }

        return rowView;
    }
}
