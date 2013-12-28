package com.mates120.dictionaryparser.debug;

import android.util.Log;

public class AndroidLogger implements Printing
{

	@Override
	public void print(String tag, String msg) {
		Log.d(tag, msg);
	}

}
