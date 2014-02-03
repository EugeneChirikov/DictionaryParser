package com.mates120.dictionaryparser;

import android.content.Context;

import com.mates120.dictionaryparser.debug.Logger;
import com.mates120.dictionaryparser.debug.SystemOut;
import com.mates120.dictionaryparser.stardict.DictFile;
import com.mates120.dictionaryparser.stardict.IdxFile;
import com.mates120.dictionaryparser.stardict.IfoFile;
import com.mates120.dictionaryparser.stardict.RawStarDictDictionary;

public class DictionaryParser {
	private static final String DICT_NAME = "dict0";
	private static final String FILES_PATH = "/storage/sdcard0/mates120";
//	private static Storage storage = null;

	public static void run(Storage storage)
	{
//		storage = storage;
		run(storage, FILES_PATH);
	}
	
	public static void run(Storage storage, String filesPath)
	{
		String dictType = "StarDict"; // should be a code to find this out
		RawDictionary rawDictionary = obtainDictionaryFiles(filesPath, dictType);
		rawDictionary.parseAndCopyIntoDB(storage);
	}
	
	public static Storage getStorage(Context context)
	{
		return new DataSource(context, DICT_NAME);
	}
	
	public static RawDictionary obtainDictionaryFiles(String filesPath, String dictType)
	{
		RawDictionary rawDict = null;
		if (dictType == "StarDict")
		{
			try
			{
				FilesFinder filesFinder = new FilesFinder();
				String ifoPath = filesFinder.getFilePath(filesPath, ".ifo");
				String idxPath = filesFinder.getFilePath(filesPath, ".idx");
				String dictPath = filesFinder.getFilePath(filesPath, ".dict");
				Logger.l().PRINT(filesPath);
				Logger.l().PRINT(ifoPath);
				Logger.l().PRINT(idxPath);
				Logger.l().PRINT(dictPath);
				
				IfoFile ifoFile = new IfoFile(ifoPath);
				IdxFile idxFile = new IdxFile(idxPath);
				DictFile dictFile = new DictFile(dictPath);
				rawDict = new RawStarDictDictionary(ifoFile, idxFile, dictFile);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return rawDict;
	}
}
