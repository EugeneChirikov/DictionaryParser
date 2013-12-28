package com.mates120.dictionaryparser;
import com.mates120.dictionaryparser.debug.AndroidLogger;
import com.mates120.dictionaryparser.debug.Logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ParseDictActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent jobServiceIntent = new Intent(this, DoTheJobService.class);
		Logger.l().setPrinter(new AndroidLogger());
		startService(jobServiceIntent);
	}
}
