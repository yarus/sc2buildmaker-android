package com.sc2toolslab.sc2bm.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.ui.model.NavDrawerMenuItem;

import java.util.Collections;
import java.util.List;

public class NavDrawerMenuAdapter extends RecyclerView.Adapter<NavDrawerMenuAdapter.MenuItemViewHolder> {
	private LayoutInflater mInflater;
	private INavDrawerItemSelectedListener mClickListener;
	private List<NavDrawerMenuItem> mMenuItems = Collections.emptyList();

	public NavDrawerMenuAdapter(Context context, List<NavDrawerMenuItem> menuItems) {
		mInflater = LayoutInflater.from(context);
		mMenuItems = menuItems;
	}

	@Override
	public NavDrawerMenuAdapter.MenuItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = mInflater.inflate(R.layout.fragment_nd_menu_item, viewGroup, false);

		return new MenuItemViewHolder(view);
	}

	@Override
	//public void onBindViewHolder(MenuItemViewHolder holder, final int i) {
	public void onBindViewHolder(MenuItemViewHolder holder, int i) {
		NavDrawerMenuItem currentItem = mMenuItems.get(i);

		holder.mTitle.setText(currentItem.Title);
		holder.mIcon.setImageResource(currentItem.IconId);
	}

	@Override
	public int getItemCount() {
		return mMenuItems.size();
	}

	public void setClickListener(INavDrawerItemSelectedListener listener) {
		mClickListener = listener;
	}

	class MenuItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		TextView mTitle;
		ImageView mIcon;

		public MenuItemViewHolder(View itemView) {
			super(itemView);

			mTitle = (TextView) itemView.findViewById(R.id.listText);
			mIcon = (ImageView) itemView.findViewById(R.id.listIcon);

			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (mClickListener != null) {
				mClickListener.onItemSelected(mMenuItems.get(getPosition()));
			}
		}
	}

	public interface INavDrawerItemSelectedListener {
		void onItemSelected(NavDrawerMenuItem item);
	}
}
