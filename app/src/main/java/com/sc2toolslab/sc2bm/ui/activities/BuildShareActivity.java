package com.sc2toolslab.sc2bm.ui.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sc2toolslab.sc2bm.dataaccess.FileStorageHelper;
import com.sc2toolslab.sc2bm.datacontracts.BuildOrderInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class BuildShareActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getIntent().getExtras().getParcelable(Intent.EXTRA_STREAM) != null) {
			Uri uri = (Uri) getIntent().getExtras().getParcelable(Intent.EXTRA_STREAM);
			ContentResolver cr = getContentResolver();
			InputStream stream = null;
			try {
				stream = cr.openInputStream(uri);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			StringBuffer fileContent = new StringBuffer();
			int ch;
			try {
				while( (ch = stream.read()) != -1)
					fileContent.append((char)ch);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String data = new String(fileContent);

			BuildOrderInfo buildOrder = null;
			boolean withErrors = false;
			try {
				buildOrder = new Gson().fromJson(data, BuildOrderInfo.class);
			} catch (Throwable e) {
				e.printStackTrace();
				withErrors = true;
				Toast.makeText(this, "Content is not supported...", Toast.LENGTH_SHORT).show();
			}
			if(buildOrder.getName() == null || "".equals(buildOrder.getName()) || buildOrder.getSC2VersionID() == null || "".equals(buildOrder.getSC2VersionID())
					|| buildOrder.getVsRace() == null || "".equals(buildOrder.getVsRace())) {
				withErrors = true;
			}
			if(!withErrors) {
				try
				{
					File saveBoFile = new File(FileStorageHelper.getBuildOrdersDirectory(), buildOrder.getName() + ".txt");
					FileWriter writer = new FileWriter(saveBoFile);
					writer.append(data);
					writer.flush();
					writer.close();

					Toast.makeText(this, "Build order added", Toast.LENGTH_SHORT).show();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			} else {
				Toast.makeText(this, "Content is not supported...", Toast.LENGTH_SHORT).show();
			}
			finish();
		} else {
			Toast.makeText(this, "Content is not supported...", Toast.LENGTH_SHORT).show();
			finish();
		}
	}
}
