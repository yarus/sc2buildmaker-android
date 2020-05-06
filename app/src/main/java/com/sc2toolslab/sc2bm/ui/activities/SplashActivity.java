package com.sc2toolslab.sc2bm.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.sc2toolslab.sc2bm.R;
import com.sc2toolslab.sc2bm.dataaccess.FileStorageHelper;
import com.sc2toolslab.sc2bm.ui.providers.BuildOrdersProvider;
import com.sc2toolslab.sc2bm.ui.utils.NavigationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashActivity extends Activity {
	private boolean mLoadingResult = false;
	private AlertDialog.Builder mAlertDlg;

	final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);

		initAlertDialog();

		if (Build.VERSION.SDK_INT >= 23) {
			// Marshmallow+
			List<String> permissionsNeeded = new ArrayList<String>();

			final List<String> permissionsList = new ArrayList<String>();
			if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
				permissionsNeeded.add("Write to Storage");
			if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
				permissionsNeeded.add("Read from Storage");
			if (!addPermission(permissionsList, Manifest.permission.INTERNET))
				permissionsNeeded.add("Internet connection");

			if (permissionsList.size() > 0) {
				if (permissionsNeeded.size() > 0) {
					// Need Rationale
					String message = "You need to grant access to " + permissionsNeeded.get(0);

					for (int i = 1; i < permissionsNeeded.size(); i++)
						message = message + ", " + permissionsNeeded.get(i);

					showMessageOKCancel(message,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
											REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
								}
							});

					return;
				}

				requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
						REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
			}

			runInitializationCode();

		} else {
			runInitializationCode();
		}
	}

	private void runInitializationCode() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids) {
				mLoadingResult = FileStorageHelper.checkDirectories(SplashActivity.this);

				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				if(mLoadingResult) {
					// Check if instance initialized
					BuildOrdersProvider.getInstance(SplashActivity.this);

					NavigationManager.startMainActivity(SplashActivity.this);

					SplashActivity.this.finish();
				} else {
					mAlertDlg.show();
				}
			}
		}.execute();
	}

	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(this)
				.setMessage(message)
				.setPositiveButton("OK", okListener)
				.setNegativeButton("Cancel", null)
				.create()
				.show();
	}

	private boolean addPermission(List<String> permissionsList, String permission) {
		if (Build.VERSION.SDK_INT < 23) {
			return true;
		}

		if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
			permissionsList.add(permission);
			// Check for Rationale Option
			return shouldShowRequestPermissionRationale(permission);
		}
		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
			{
				Map<String, Integer> perms = new HashMap<String, Integer>();
				// Initial
				perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
				// Fill with results
				for (int i = 0; i < permissions.length; i++)
					perms.put(permissions[i], grantResults[i]);
				// Check for ACCESS_FINE_LOCATION
				if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
						&& perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
						&& perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
					// All Permissions Granted

					runInitializationCode();

				} else {
					// Permission Denied
					Toast.makeText(this, "Some Permission is Denied", Toast.LENGTH_SHORT).show();
				}
			}
			break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	private void initAlertDialog() {
		mAlertDlg = new AlertDialog.Builder(this);
		mAlertDlg.setTitle("Configuration loading error!");
		mAlertDlg.setMessage("Configuration load failed. Please check that external storage is available (your device isn\\'t connected to PC as a flash drive)");
		mAlertDlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				finish();
			}
		});
		mAlertDlg.setCancelable(false);
	}
}