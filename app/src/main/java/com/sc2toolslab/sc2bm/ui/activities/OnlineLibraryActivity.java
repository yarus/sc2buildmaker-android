package com.sc2toolslab.sc2bm.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.datacontracts.BuildOrderInfo;
import com.sc2toolslab.sc2bm.domain.BuildOrderEntity;
import com.sc2toolslab.sc2bm.ui.adapters.BuildItemsListAdapter;
import com.sc2toolslab.sc2bm.ui.adapters.BuildOrderListAdapter;
import com.sc2toolslab.sc2bm.ui.presenters.OnlineLibraryPresenter;
import com.sc2toolslab.sc2bm.ui.views.IOnlineLibraryView;

import java.util.ArrayList;

public class OnlineLibraryActivity extends FragmentListActivity implements IOnlineLibraryView {
	private OnlineLibraryPresenter mPresenter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_library);

		Toolbar mToolbar = (Toolbar) findViewById(R.id.appBar);
		setSupportActionBar(mToolbar);

		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mPresenter = new OnlineLibraryPresenter(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.searchable.
		int id = item.getItemId();

		if (id == android.R.id.home) {
			finish();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public Context getContext() {
		return OnlineLibraryActivity.this;
	}

	@Override
	public void renderList(ArrayList<BuildOrderEntity> buildItems) {
		if(getListView().getAdapter() == null) {
			setListAdapter(new BuildOrderListAdapter(this, buildItems));
		} else {
			((BuildOrderListAdapter)getListView().getAdapter()).updateData(buildItems);
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
