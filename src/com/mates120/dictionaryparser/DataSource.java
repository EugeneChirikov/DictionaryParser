package com.mates120.dictionaryparser;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource {
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	
	private String[] allWordsColumns = {DatabaseHelper.COL_WORDS_ID, 
			DatabaseHelper.COL_WORDS_SOURCE, 
			DatabaseHelper.COL_WORDS_SEARCH_COUNTER, 
			DatabaseHelper.COL_WORDS_RATING};
	
	private String[] allValuesColumns = {DatabaseHelper.COL_VALUES_ID, 
			DatabaseHelper.COL_VALUES_SOURCE,
			DatabaseHelper.COL_VALUES_DICT_ID};
	
	private String[] allLinksColumns = {DatabaseHelper.COL_LINKS_WORD,
			DatabaseHelper.COL_LINKS_VALUE};
	
	private String[] allDictionariesColumns = {DatabaseHelper.COL_DICTIONARIES_ID,
			DatabaseHelper.COL_DICTIONARIES_NAME, DatabaseHelper.COL_DICTIONARIES_ISACT};
	
	public DataSource(Context context){
		dbHelper = new DatabaseHelper(context);
	}
	
	public void open() throws SQLException{
		database = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public long insertWord(String wordSource){
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COL_WORDS_SOURCE, wordSource);
		values.put(DatabaseHelper.COL_WORDS_SEARCH_COUNTER, 0);
		values.put(DatabaseHelper.COL_WORDS_RATING, 0);
		return database.insert(DatabaseHelper.TABLE_WORDS, null, values);
	}
	
	public void deleteWordById(long wordId){
		System.out.println("Word deleted with id: " + wordId);
		database.delete(DatabaseHelper.TABLE_WORDS, 
				DatabaseHelper.COL_WORDS_ID + " = " + wordId, null);
	}
	
	public Word getWord(String source){
		Word foundWord = null;
		Cursor cursor = database.query(DatabaseHelper.TABLE_WORDS, allWordsColumns, 
				DatabaseHelper.COL_WORDS_SOURCE + " = ?",
				new String[]{source}, null, null, null);
		cursor.moveToFirst();
		foundWord = cursorToWord(cursor);
		cursor.close();
		return foundWord;
	}
	
	private Word cursorToWord(Cursor cursor){
		Word word = null;
		if(cursor != null && cursor.getCount() > 0){
			word = new Word();
			word.setId(cursor.getLong(0));
			word.setSource(cursor.getString(1));
			word.setSearchCount(cursor.getInt(2));
			word.setRating(cursor.getInt(3));
		}
		return word;
	}
	
	public long insertValue(String value, String dictionaryName){
		/*
		 * Insert one value removed for insertValues
		 * 
		 * */
		long dictId = getDictionaryId(dictionaryName);
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COL_VALUES_SOURCE, value);
		values.put(DatabaseHelper.COL_VALUES_DICT_ID, dictId);
		return database.insert(DatabaseHelper.TABLE_VALUES, null, values);
	}
	
	public long[] insertValues(List<String> sourceValues, String dictionaryName){
		/*
		 * Insert list of values.
		 */
		long valueIds[] = new long[sourceValues.size()];
		long dictId = getDictionaryId(dictionaryName);
		for (int i = 0; i < sourceValues.size(); i++){
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COL_VALUES_SOURCE, sourceValues.get(i));
			values.put(DatabaseHelper.COL_VALUES_DICT_ID, dictId);
			valueIds[i] = database.insert(DatabaseHelper.TABLE_VALUES, null, values);
		}
		return valueIds;
	}
	
	public void deleteDictionaryValues(String dictionaryName){
		long dictId = getDictionaryId(dictionaryName);
		System.out.println("Value deleted with dict_name: " + dictionaryName);
		database.delete(DatabaseHelper.TABLE_VALUES, 
				DatabaseHelper.COL_VALUES_DICT_ID + " = " + dictId, null);
	}
	
	public long[] getDictionaryValuesIds(String dictionaryName){
		long valuesIds[];
		long dictionaryId = getDictionaryId(dictionaryName);
		Cursor cursor = database.query(DatabaseHelper.TABLE_VALUES, new String[]{DatabaseHelper.COL_VALUES_ID}, 
				DatabaseHelper.COL_VALUES_DICT_ID + " = ?",
				new String[]{String.valueOf(dictionaryId)}, null, null, null);
		valuesIds = new long[cursor.getCount()];
		cursor.moveToFirst();
		for (int i = 0; !cursor.isAfterLast(); i++){
			valuesIds[i] = cursor.getLong(0);
			cursor.moveToNext();
		}
		cursor.close();
		return valuesIds;
	}
	
	public List<Value> getWordValues(long wordId){
		List<Value> values = new ArrayList<Value>();
		Cursor cursor = database.rawQuery("select " + "val_s." + allValuesColumns[0] 
				+", " + allValuesColumns[1] + ", " + allValuesColumns[2] 
				+ " from "	+ DatabaseHelper.TABLE_VALUES
				+ " val_s inner join " + DatabaseHelper.TABLE_LINKS
				+ " links ON links."+ DatabaseHelper.COL_LINKS_VALUE 
				+ " = val_s." + DatabaseHelper.COL_VALUES_ID 
				+ " inner join " + DatabaseHelper.TABLE_DICTIONARIES
				+ " dicts ON val_s." + DatabaseHelper.COL_VALUES_DICT_ID
				+ " = dicts." + DatabaseHelper.COL_DICTIONARIES_ID
				+ " where " + DatabaseHelper.COL_DICTIONARIES_ISACT
				+ " = 1 AND "
				+ DatabaseHelper.COL_LINKS_WORD + " = " + wordId, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			Value value = cursorToValue(cursor);
			values.add(value);
			cursor.moveToNext();
		}
		cursor.close();
		return values;
	}
	
	private Value cursorToValue(Cursor cursor){
		Value value = null;
		if(cursor != null && cursor.getCount() > 0){
			value = new Value();
			value.setId(cursor.getLong(0));
			value.setValue(cursor.getString(1));
			value.setDictionary(getDictionaryName(cursor.getLong(2)));
		}
		return value;
	}
	
	public void createLink(long wordId, long valueId){
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COL_LINKS_WORD, wordId);
		values.put(DatabaseHelper.COL_LINKS_VALUE, valueId);
		database.insert(DatabaseHelper.TABLE_LINKS, null, values);
	}
	
	public void deleteLinkByValue(long valueId){
		System.out.println("Link deleted with value id: " + valueId);
		database.delete(DatabaseHelper.TABLE_LINKS, 
				DatabaseHelper.COL_LINKS_VALUE + " = " + valueId, null);
	}
	
	public long getWordIdByValueId(long valueId){
		long wordId;
		Cursor cursor = database.query(DatabaseHelper.TABLE_LINKS, 
				allLinksColumns, DatabaseHelper.COL_LINKS_VALUE + " = ?",
				new String[]{String.valueOf(valueId)}, null, null, null);
		cursor.moveToFirst();
		wordId = cursor.getLong(0);
		cursor.close();
		return wordId;
	}
	
	public boolean wordHasOneValue(long wordId){
		boolean isOne = false;
		Cursor cursor = database.query(DatabaseHelper.TABLE_LINKS,
				allLinksColumns, DatabaseHelper.COL_LINKS_WORD + " = ?",
				new String[]{String.valueOf(wordId)}, null, null, null);
		if(cursor.getCount() == 1)
			isOne = true;
		return isOne;
	}
	
	public long insertDictionary(String name){
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COL_DICTIONARIES_NAME, name);
		values.put(DatabaseHelper.COL_DICTIONARIES_ISACT, 1);
		return database.insert(DatabaseHelper.TABLE_DICTIONARIES, null, values);
	}
	
	public long deleteDictionaryByName(String name){
		long id = getDictionaryId(name);
		System.out.println("Dictionary deleted with id: " + id);
		database.delete(DatabaseHelper.TABLE_DICTIONARIES, 
				DatabaseHelper.COL_VALUES_ID + " = " + id, null);
		return id;
	}
	
	public String getDictionaryName(long dictId){
		String name = null;
		Cursor cursor = database.query(DatabaseHelper.TABLE_DICTIONARIES,
				allDictionariesColumns, DatabaseHelper.COL_DICTIONARIES_ID + " = ?",
				new String[]{String.valueOf(dictId)}, null, null, null);
		if(cursor != null && cursor.getCount() > 0){
			cursor.moveToFirst();
			name = cursor.getString(1);
			cursor.close();
		}
		return name;
	}
	
	public long getDictionaryId(String dictName){
		long id = 0;
		Cursor cursor = database.query(DatabaseHelper.TABLE_DICTIONARIES,
				allDictionariesColumns, DatabaseHelper.COL_DICTIONARIES_NAME + " = ?",
				new String[]{dictName}, null, null, null);
		try{
			if(cursor != null && cursor.getCount() > 0){
				cursor.moveToFirst();
				id = cursor.getLong(0);
			}
		}finally{
			cursor.close();
		}
		return id;
	}
	
	public boolean dictionaryInDB(String dictName){
		boolean inDB = false;
		Cursor cursor = database.query(DatabaseHelper.TABLE_DICTIONARIES,
				allDictionariesColumns, DatabaseHelper.COL_DICTIONARIES_NAME + " = ?",
				new String[]{dictName}, null, null, null);
		try{
			if(cursor != null && cursor.getCount() > 0)
				inDB = true;
		}finally{
			cursor.close();
		}
		return inDB;
	}
	
	 public List<Dictionary> getAllDictionaries() {
		    List<Dictionary> dictionaries = new ArrayList<Dictionary>();

		    Cursor cursor = database.query(DatabaseHelper.TABLE_DICTIONARIES,
		        allDictionariesColumns, null, null, null, null, null);

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		    	Dictionary currentDict = 
		    			new Dictionary(cursor.getString(1), cursor.getInt(2));
		    	dictionaries.add(currentDict);
		    	cursor.moveToNext();
		    }
		    cursor.close();
		    return dictionaries;
		  }
	 
	 public long setSearchInDict(String name, boolean searchIn){
		 ContentValues values = new ContentValues();
		 int value = 0;
		 if (searchIn) value = 1;
		 values.put(DatabaseHelper.COL_DICTIONARIES_ISACT, value);
		 return database.update(DatabaseHelper.TABLE_DICTIONARIES, values,
				 DatabaseHelper.COL_DICTIONARIES_NAME + "=?", new String[]{name});
	 }
}
