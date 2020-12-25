package com.sc2toolslab.sc2bm.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.domain.BuildItemEntity;
import com.sc2toolslab.sc2bm.engine.domain.BuildItemStatistics;
import com.sc2toolslab.sc2bm.ui.model.AddBuildItemHolder;
import com.sc2toolslab.sc2bm.ui.providers.BuildItemImageProvider;

import java.util.ArrayList;
import java.util.List;

public class StatisticsListAdapter extends ArrayAdapter<BuildItemEntity> {
    private Context mContext;
    private List<BuildItemEntity> mBuildItems = new ArrayList<BuildItemEntity>();
    private BuildItemStatistics mCurrentStats;
    private BuildItemImageProvider mImageProvider;

    private int mItemHeight = 0;
    private int mNumColumns = 0;

    public StatisticsListAdapter(Context context, List<BuildItemEntity> buildItems, BuildItemStatistics currentStats) {
        super(context, R.layout.fragment_build_maker_add_item, buildItems);

        this.mContext = context;
        this.mBuildItems = buildItems;
        this.mCurrentStats = currentStats;

        this.mImageProvider = new BuildItemImageProvider();
    }

    public void updateData(List<BuildItemEntity> buildItems, BuildItemStatistics currentStats) {
        //values = data;
        List<BuildItemEntity> newItems = new ArrayList<>(buildItems);

        this.clear();

        for(BuildItemEntity item : newItems) {
            this.add(item);
        }

        this.mBuildItems = buildItems;
        this.mCurrentStats = currentStats;

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

            holder.txtItemName.setText(itemName);

            if (statValue > 0) {
                holder.txtItemCount.setText(Integer.toString(statValue));
            } else {
                holder.txtItemCount.setText("");
            }
        }

        return rowView;
    }
}
