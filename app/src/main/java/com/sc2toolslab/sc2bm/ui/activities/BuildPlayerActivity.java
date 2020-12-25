package com.sc2toolslab.sc2bm.ui.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.constants.AppConstants;
import com.sc2toolslab.sc2bm.domain.RaceEnum;
import com.sc2toolslab.sc2bm.engine.domain.BuildOrderProcessorItem;
import com.sc2toolslab.sc2bm.ui.adapters.BuildItemsListAdapter;
import com.sc2toolslab.sc2bm.ui.presenters.BuildPlayerPresenter;
import com.sc2toolslab.sc2bm.ui.providers.BuildItemImageProvider;
import com.sc2toolslab.sc2bm.ui.utils.UiDataViewHelper;
import com.sc2toolslab.sc2bm.ui.views.IBuildPlayerView;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class BuildPlayerActivity extends FragmentListActivity implements IBuildPlayerView, TextToSpeech.OnInitListener {
	private BuildPlayerPresenter mPresenter;
	private BuildItemImageProvider mImageProvider;

	private TextToSpeech mTts;

	private ImageView mImgPlayPause;
	private SeekBar mCtlrSeekBar;
	private TextView mTxtTimer;
	private TextView mTxtFinish;

	private Dialog mShowItemDlg;
	private View mItemInfoLayout;

	private boolean _isDraging = false;
	private Boolean _wasPlayingBeforeStartedToTrack = false;

	//region Lifecycle handlers

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_build_player);

		Toolbar mToolbar = (Toolbar) findViewById(R.id.appBar);
		setSupportActionBar(mToolbar);

		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		_initControls();

		mTts = new TextToSpeech(this, this);

		mImageProvider = new BuildItemImageProvider();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		String buildName = getIntent().getStringExtra(AppConstants.BUILD_ORDER_NAME_INTENT_KEY);
		boolean showWorkers = prefs.getBoolean("ShowWorkers", true);
		String gameSpeed = prefs.getString(getString(R.string.pref_game_speed), "faster");
		int startSecond = Integer.valueOf(prefs.getString(getString(R.string.pref_start_second), AppConstants.DEFAULT_START_SECOND));

		mPresenter = new BuildPlayerPresenter(this, buildName, showWorkers, gameSpeed, startSecond);

		if (savedInstanceState != null) {
			boolean isPlaying = savedInstanceState.getBoolean("IsPlaying");
			int currentSecond = savedInstanceState.getInt("CurrentSecond");

			mPresenter.setCurrentSecond(currentSecond);

			if (isPlaying && !mPresenter.getIsPlaying()) {
				mPresenter.changePlayMode();
			}
		}
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();

		_initControls();

		mTts = new TextToSpeech(this, this);
	}

	@Override
	public void onBackPressed() {
		_navigateBack();
	}

	@Override
	protected void onPause() {
		if ((mShowItemDlg != null) && mShowItemDlg.isShowing()) {
			mShowItemDlg.dismiss();
		}

		mShowItemDlg = null;

		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("IsPlaying", mPresenter.getIsPlaying());
		outState.putInt("CurrentSecond", mPresenter.getCurrentSecond());
	}

	@Override
	protected void onDestroy() {
		if (mTts != null) {
			mTts.stop();
			mTts.shutdown();
		}

		mPresenter.formDestroying();

		super.onDestroy();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem itemShowWorkers = menu.findItem(R.id.action_show_workers);

		if (mPresenter.getShowWorkers()) {
			if (mPresenter.getCurrentFaction() == RaceEnum.Terran) {
				itemShowWorkers.setIcon(R.drawable.bi_scv);
			} else if (mPresenter.getCurrentFaction() == RaceEnum.Protoss) {
				itemShowWorkers.setIcon(R.drawable.bi_probe);
			} else if (mPresenter.getCurrentFaction() == RaceEnum.Zerg) {
				itemShowWorkers.setIcon(R.drawable.bi_drone);
			}
		} else {
			if (mPresenter.getCurrentFaction() == RaceEnum.Terran) {
				itemShowWorkers.setIcon(R.drawable.bi_scv_bw);
			} else if (mPresenter.getCurrentFaction() == RaceEnum.Protoss) {
				itemShowWorkers.setIcon(R.drawable.bi_probe_bw);
			} else if (mPresenter.getCurrentFaction() == RaceEnum.Zerg) {
				itemShowWorkers.setIcon(R.drawable.bi_drone_bw);
			}
		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_buildplayer, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.searchable.
		int id = item.getItemId();

		if (id == R.id.action_show_workers) {
			mPresenter.changeWorkersFilter();
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putBoolean("ShowWorkers", mPresenter.getShowWorkers());
			edit.apply();
			invalidateOptionsMenu();
			return true;
		}

		if (id == android.R.id.home) {
			_navigateBack();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = mTts.setLanguage(Locale.ENGLISH);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				result = mTts.setLanguage(Locale.US);
				if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
					result = mTts.setLanguage(Locale.UK);
					if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
						Log.e("TTS", "This language is not supported");
					}
				}
			}
		} else {
			this.showMessage("TextToSpeech android feature is not enabled on your device! Please make sure to enable it to be able to use Voice Helper feature of SC2BuildMaker app.");
		}
	}

	//endregion

	//region Event Handlers

	public void onPlayPause(View view) {
		mPresenter.changePlayMode();
	}

	public void onReset(View view) {
		mPresenter.stopPlayer();
	}

	//endregion

	//region IBuildPlayerView implementation

	@Override
	public void setSelectedPosition(int position) {
		final int newPosition = position;

		getListView().post(new Runnable() {
			@Override
			public void run() {
				((BuildItemsListAdapter) getListView().getAdapter()).setSelectedIndex(newPosition);
				getListView().setSelection(newPosition);
				getListView().smoothScrollToPositionFromTop(newPosition, 0, 0);
			}
		});
	}

	@Override
	public void speak(String text) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mTts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
		} else {
			mTts.speak(text, TextToSpeech.QUEUE_ADD, null);
		}
	}

	@Override
	public void showItemInfo(BuildOrderProcessorItem buildItem) {
		if (mItemInfoLayout == null) {
			mItemInfoLayout = getLayoutInflater().inflate(R.layout.fragment_build_maker_add_item_info, (ViewGroup) findViewById(R.id.llToast));
		}

		_setUnitImageForItem(mItemInfoLayout, buildItem);
		_setItemTitle(mItemInfoLayout, buildItem);

		if (mShowItemDlg == null) {
			mShowItemDlg = new Dialog(this, R.style.dialogTheme) {
				@Override
				public boolean dispatchTouchEvent(MotionEvent ev) {
					dismiss();

					return super.dispatchTouchEvent(ev);
				}
			};
			mShowItemDlg.setContentView(mItemInfoLayout);
		}

		mShowItemDlg.show();

		final Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				if (mShowItemDlg != null && mShowItemDlg.isShowing()) {
					mShowItemDlg.dismiss(); // when the task active then close the dialog
				}

				t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
			}
		}, 3 * 1000);
	}

	@Override
	public void showMessage(String message) {
		Toast.makeText(BuildPlayerActivity.this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void setBuildName(String buildName) {
		//noinspection ConstantConditions
		getSupportActionBar().setTitle(buildName);
	}

	@Override
	public void renderList(List<BuildOrderProcessorItem> buildItems) {
		if (getListView().getAdapter() == null) {
			setListAdapter(new BuildItemsListAdapter(this, buildItems));
		} else {
			int selectedIndex = mPresenter.getSelectedPosition();
			((BuildItemsListAdapter) getListView().getAdapter()).updateData(buildItems, selectedIndex);
		}
	}

	@Override
	public void setFinishSecond(int finishSecond) {
		mTxtFinish.setText(UiDataViewHelper.getTimeStringFromSeconds(finishSecond));
		mCtlrSeekBar.setMax(finishSecond);
	}

	@Override
	public void setCurrentSecond(int currentSecond) {
		mTxtTimer.setText(UiDataViewHelper.getTimeStringFromSeconds(currentSecond));
		mCtlrSeekBar.setProgress(currentSecond);
	}

	@Override
	public void setPlayPauseImage(boolean isPlaying) {
		if (isPlaying) {
			mImgPlayPause.setImageResource(R.drawable.pause);
		} else {
			mImgPlayPause.setImageResource(R.drawable.play);
		}
	}

	@Override
	public void setKeepScreenFlag(boolean keepScreen) {
		if(keepScreen) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	//endregion

	//region private methods

	private void _initControls() {
		mImgPlayPause = (ImageView) findViewById(R.id.imgPlayPause);
		mCtlrSeekBar = (SeekBar) findViewById(R.id.ctlrSeekBar);

		mCtlrSeekBar.setOnSeekBarChangeListener(
				new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
						if (_isDraging) {
							mPresenter.setCurrentSecond(progresValue);
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						_isDraging = true;
						_wasPlayingBeforeStartedToTrack = mPresenter.getIsPlaying();
						if (mPresenter.getIsPlaying()) {
							mPresenter.changePlayMode();
						}
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						_isDraging = false;
						if (_wasPlayingBeforeStartedToTrack) {

							if (!mPresenter.getIsPlaying()) {
								mPresenter.changePlayMode();
							}
							//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
						} else {
							//getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
						}
					}
				});

		mTxtTimer = (TextView) findViewById(R.id.txtTimer);
		mTxtFinish = (TextView) findViewById(R.id.txtFinish);

		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				mPresenter.setSelectedPosition(arg2);
			}
		});
	}

	private void _navigateBack() {
		finish();
	}

	private void _setUnitImageForItem(View layout, BuildOrderProcessorItem item) {
		ImageView image = (ImageView) layout.findViewById(R.id.toast_image);

		Integer imageId = mImageProvider.getImageResourceIdByKey(item.getItemName());
		if (imageId != null) {
			image.setImageResource(imageId);
		} else {
			image.setImageResource(R.drawable.empty_cell);
		}
	}

	private void _setItemTitle(View layout, BuildOrderProcessorItem item) {
		TextView title = (TextView) layout.findViewById(R.id.txtName);
		title.setText(item.getDisplayName());
	}

	@Override
	public Context getContext() {
		return this;
	}

	//endregion
}