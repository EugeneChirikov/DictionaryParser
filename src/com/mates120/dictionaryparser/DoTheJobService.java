package com.mates120.dictionaryparser;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class DoTheJobService extends IntentService
{

	public DoTheJobService() {
		super("ParseAndCopyIntoDB");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0)
	{

		Log.d("STARD_PARSER", "Service is started woo hoo!");
		PowerManager mgr = (PowerManager)getBaseContext().getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DictParserWakeLock");
		wakeLock.acquire();
		try
		{
			DictionaryParser.run(DictionaryParser.getStorage(getBaseContext()));
		}
		finally
		{
			wakeLock.release();
		}
	}
}
