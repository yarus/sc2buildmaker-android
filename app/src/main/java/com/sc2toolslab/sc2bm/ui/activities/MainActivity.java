package com.sc2toolslab.sc2bm.ui.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.ui.fragments.NavDrawerFragment;
import com.sc2toolslab.sc2bm.ui.adapters.MainActivityViewPagerAdapter;
import com.sc2toolslab.sc2bm.ui.presenters.MainPresenter;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.providers.FactionImageProvider;
import com.sc2toolslab.sc2bm.ui.providers.IImageProvider;
import com.sc2toolslab.sc2bm.ui.utils.FloatingActionButton;
import com.sc2toolslab.sc2bm.ui.utils.NavigationManager;
import com.sc2toolslab.sc2bm.ui.views.IMainView;

public class MainActivity extends AppCompatActivity implements IMainView, SearchView.OnQueryTextListener {
	private IImageProvider<RaceEnum> mFactionImageProvider;

	private ViewPager mPager;
	private TabLayout mTabHost;
	private MainPresenter mPresenter;
	private DrawerLayout mDrawerLayout;
	private EditText mTxtSearch;
	private MainActivityViewPagerAdapter mAdapter;
	private FloatingActionButton mBtnNewBuild;
	private Toolbar mToolbar;
	private NavDrawerFragment mNavDrawerFragment;

	private int mSelectedTab;
	private boolean mTileView;
	private boolean mShowDefaultBuilds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		_initUserControls();
		_initMembers();

		setSupportActionBar(mToolbar);
		//noinspection ConstantConditions
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		_setupControls();

		//_loadTabs();

		if (savedInstanceState != null) {
			mSelectedTab = savedInstanceState.getInt("SelectedTab");
			mPager.setCurrentItem(mSelectedTab);
			mTabHost.getTabAt(mSelectedTab).select();
		}
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();

		int selectedTab = mSelectedTab;

		_initUserControls();
		_setupControls();

		mSelectedTab = selectedTab;

		mPager.setCurrentItem(mSelectedTab);
		mTabHost.getTabAt(mSelectedTab).select();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mSelectedTab = savedInstanceState.getInt("SelectedTab");
			mTabHost.getTabAt(mSelectedTab).select();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("SelectedTab", mTabHost.getSelectedTabPosition());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);

		MenuItem itemFaction = menu.findItem(R.id.action_faction);
		if (itemFaction != null) {
			itemFaction.setIcon(new FactionImageProvider().getImageResourceIdByKey(BuildOrdersProvider.getInstance(MainActivity.this).getFactionFilter()));
		}

		final MenuItem item = menu.findItem(R.id.action_search);
		if (item != null) {
			final View v = item.getActionView();
			mTxtSearch = (EditText) v.findViewById(R.id.txt_search);

			MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
				@Override
				public boolean onMenuItemActionExpand(final MenuItem item) {
					mTxtSearch.addTextChangedListener(new TextWatcher() {
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count, int after) {
						}

						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
						}

						@Override
						public void afterTextChanged(Editable s) {
							mPresenter.onBuildSearch(s.toString());
						}
					});

					_changeKeyboardVisibility(true);

					Button btnClose = (Button) v.findViewById(R.id.btnClear);
					if (btnClose != null) {
						btnClose.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								MenuItemCompat.collapseActionView(item);
							}
						});
					}

					return true;
				}

				@Override
				public boolean onMenuItemActionCollapse(MenuItem item) {
					mTxtSearch.setText("");
					mPresenter.onBuildSearch("");

					_changeKeyboardVisibility(false);

					return true;
				}
			});

			String currentFilter = BuildOrdersProvider.getInstance(MainActivity.this).getTitleFilter();
			if (!"".equals(currentFilter)) {
				mTxtSearch.setText(currentFilter);
				mTxtSearch.requestFocus();
				item.expandActionView();
			}
		}

		MenuItem itemListView = menu.findItem(R.id.action_view_list);
		MenuItem itemTileView = menu.findItem(R.id.action_view_tile);
		if (itemListView != null && itemTileView != null) {
			itemTileView.setVisible(!mTileView);
			itemListView.setVisible(mTileView);
		}

		MenuItem itemHideDefault = menu.findItem(R.id.action_default_hide);
		MenuItem itemShowDefault = menu.findItem(R.id.action_default_show);
		if (itemHideDefault != null && itemShowDefault != null) {
			itemShowDefault.setVisible(!mShowDefaultBuilds);
			itemHideDefault.setVisible(mShowDefaultBuilds);
		}

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem itemFaction = menu.findItem(R.id.action_faction);
		itemFaction.setIcon(mFactionImageProvider.getImageResourceIdByKey(mPresenter.getMainFaction()));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.searchable.
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			showAppSettings();
			return true;
		}

		if (id == R.id.action_faction_protoss) {
			_saveFactionPreference(RaceEnum.Protoss);
			return true;
		}

		if (id == R.id.action_faction_terran) {
			_saveFactionPreference(RaceEnum.Terran);
			return true;
		}

		if (id == R.id.action_faction_zerg) {
			_saveFactionPreference(RaceEnum.Zerg);
			return true;
	}

		if (id == R.id.action_faction_random) {
			_saveFactionPreference(RaceEnum.NotDefined);
			return true;
		}

		if (id == R.id.action_view_list) {
			mTileView = false;
			_saveTileViewPreference(false);
			mAdapter.setTileView(false);

			int selectedTab = mSelectedTab;

			_initUserControls();
			_setupControls();
			updateActionBar();

			mSelectedTab = selectedTab;

			mPager.setCurrentItem(mSelectedTab);

			return true;
		}

		if (id == R.id.action_view_tile) {
			mTileView = true;
			_saveTileViewPreference(true);
			mAdapter.setTileView(true);

			int selectedTab = mSelectedTab;

			_initUserControls();
			_setupControls();
			updateActionBar();

			mSelectedTab = selectedTab;

			mPager.setCurrentItem(mSelectedTab);

			return true;
		}

		if (id == R.id.action_default_hide) {
			mShowDefaultBuilds = false;
			_saveShowDefaultPreference(false);

			int selectedTab = mSelectedTab;

			_initUserControls();
			_setupControls();
			updateActionBar();

			mSelectedTab = selectedTab;

			mPager.setCurrentItem(mSelectedTab);
			mTabHost.getTabAt(mSelectedTab).select();

			return true;
		}

		if (id == R.id.action_default_show) {
			mShowDefaultBuilds = true;
			_saveShowDefaultPreference(true);

			int selectedTab = mSelectedTab;

			_initUserControls();
			_setupControls();
			updateActionBar();

			mSelectedTab = selectedTab;

			mPager.setCurrentItem(mSelectedTab);
			mTabHost.getTabAt(mSelectedTab).select();

			return true;
		}

		if (id == R.id.action_online) {
			updateActionBar();
			NavigationManager.startOnlineLibraryActivity(MainActivity.this);

			return true;
		}

		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		mPresenter.onBuildSearch(newText);
		mTxtSearch.requestFocus();

		return true;
	}

	public void showDialog(String title, int messageResourceId) {
		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

		dlgAlert.setMessage(getResources().getString(messageResourceId));
		dlgAlert.setTitle(title);
		dlgAlert.setPositiveButton("OK", null);
		dlgAlert.setCancelable(false);
		dlgAlert.create().show();
	}

	@Override
	public void showMessage(int messageResourceId) {
		Toast.makeText(this, getResources().getString(messageResourceId), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void showAppSettings() {
		NavigationManager.startSettingsActivity(this);
	}

	@Override
	public boolean safeRunActivity(Intent activity) {
		try
		{
			startActivity(activity);
			return true;
		}
		catch (ActivityNotFoundException e)
		{
			return false;
		}
	}

	@Override
	public void updateActionBar() {
		invalidateOptionsMenu();
	}

	@Override
	public void setNavigationDrawerVisible(boolean visible) {
		if (visible) {
			mDrawerLayout.openDrawer(Gravity.START);
		} else {
			mDrawerLayout.closeDrawers();
		}
	}


	private void _initUserControls() {
		mToolbar = (Toolbar) findViewById(R.id.appBar);
		mNavDrawerFragment = (NavDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_nav_drawer);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

		mPager = (ViewPager) findViewById(R.id.pager);
		setupViewPager();

		mTabHost = (TabLayout) findViewById(R.id.tabs);
		mTabHost.setupWithViewPager(mPager);

		for (int i = 0;i < mAdapter.getCount();i++) {
			mTabHost.getTabAt(i).setIcon(mAdapter.getIcon(i));
		}

		mPager.clearOnPageChangeListeners();
		mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i1) {
			}

			@Override
			public void onPageSelected(int i) {
				if (mSelectedTab != i) {
					mSelectedTab = i;
					mTabHost.getTabAt(i).select();
				}
			}

			@Override
			public void onPageScrollStateChanged(int i) {

			}
		});

		mTabHost.clearOnTabSelectedListeners();

		mTabHost.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				int i = tab.getPosition();

				if (mSelectedTab != i) {
					mSelectedTab = i;
					mPager.setCurrentItem(mSelectedTab);
					//mTabHost.getTabAt(i).select();
				}
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
	}

	private void setupViewPager() {
		mAdapter = new MainActivityViewPagerAdapter(getSupportFragmentManager(), mTileView);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(mSelectedTab);
	}

	private void _initMembers() {
		mFactionImageProvider = new FactionImageProvider();
		mPresenter = new MainPresenter(this);
		mBtnNewBuild =
				new FloatingActionButton.Builder(this)
				.withDrawable(getResources().getDrawable(android.R.drawable.ic_input_add))
				.withButtonColor(Color.DKGRAY)
				.withGravity(Gravity.BOTTOM | Gravity.CENTER)
				.withMargins(0, 0, 0, 0)
				.create();

		ViewGroup.LayoutParams lo = mBtnNewBuild.getLayoutParams();
		lo.width = 250;
		lo.height = 250;

		mBtnNewBuild.setLayoutParams(lo);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mTileView = prefs.getBoolean("TileView", true);
		mShowDefaultBuilds = prefs.getBoolean("ShowDefaultBuilds", true);
	}

	private void _setupControls() {
		mNavDrawerFragment.setRetainInstance(true);
		mNavDrawerFragment.setUp(mDrawerLayout, mToolbar, mBtnNewBuild, mPresenter);

		/*
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				mSelectedTab = position;
				mTabHost.setSelectedNavigationItem(position);
			}
		});
		*/

		_setNewBuildButton();
	}

	private void _setNewBuildButton() {
		if (mPresenter.getMainFaction() != RaceEnum.NotDefined) {
			mBtnNewBuild.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Toast.makeText(v.getContext(), "Create new build order", Toast.LENGTH_SHORT).show();
					return false;
				}
			});

			mBtnNewBuild.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					NavigationManager.startBuildMakerActivity(MainActivity.this, "");
				}
			});
			mBtnNewBuild.setFloatingActionButtonColor(Color.DKGRAY);
		} else {
			mBtnNewBuild.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(v.getContext(), "Please select your faction first.", Toast.LENGTH_SHORT).show();
				}
			});
			mBtnNewBuild.setFloatingActionButtonColor(Color.GRAY);
		}
	}

	private void _changeKeyboardVisibility(boolean visible) {
		if(visible) {
			mTxtSearch.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		} else {
			mTxtSearch.clearFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mTxtSearch.getWindowToken(), 0);
		}
	}

	private void _saveTileViewPreference(boolean value) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("TileView", value);
		editor.apply();
	}

	private void _saveShowDefaultPreference(boolean value) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("ShowDefaultBuilds", value);
		editor.apply();
	}

	private void _saveFactionPreference(final RaceEnum faction) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("SelectedFaction", faction.name());
		editor.apply();

		mPresenter.setMainFaction(faction);
		_setNewBuildButton();
	}

	@Override
	public Context getContext() {
		return this;
	}
}
