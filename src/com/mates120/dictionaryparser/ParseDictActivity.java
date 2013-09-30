package com.mates120.dictionaryparser;
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
		startService(jobServiceIntent);
	}
}
