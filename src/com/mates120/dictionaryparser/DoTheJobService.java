package com.mates120.dictionaryparser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DoTheJobService extends IntentService
{
	private final String DICT_NAME = "dict0";
	private final String FILES_PATH = "/storage/sdcard0/mates120"; 

	public DoTheJobService() {
		super("ParseAndCopyIntoDB");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		String dictType = "StarDict"; // assume this we can get with Intent
		Log.d("STARD_PARSER", "Service is started woo hoo!");
		RawDictionary rawDictionary = obtainDictionaryFiles(dictType);
		DataSource ds = new DataSource(getBaseContext(), DICT_NAME);
		rawDictionary.parseAndCopyIntoDB(ds);
	}

	private RawDictionary obtainDictionaryFiles(String dictType)
	{
		RawDictionary rawDict = null;
		if (dictType == "StarDict")
		{
			try
			{
				InputStream ifoFile = obtainFileFromFsystem(DICT_NAME + ".ifo");
//				readFileStream2(ifoFile);
				InputStream idxFile = obtainFileFromFsystem(DICT_NAME + ".idx");
//				readFileStream2(idxFile);
				InputStream dictFile = obtainFileFromFsystem(DICT_NAME + ".dict");
//				readFileStream2(dictFile);
				rawDict = new RawStarDictDictionary(ifoFile, idxFile, dictFile);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return rawDict;
	}
	
	private InputStream obtainFileFromFsystem(String name) throws FileNotFoundException
	{
		FileInputStream fis = new FileInputStream(FILES_PATH + "/" + name);
		return fis;
	}	

}
