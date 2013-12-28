package com.mates120.dictionaryparser;

import com.mates120.dictionaryparser.Exceptions.DictionaryParserException;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource implements Storage
{
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private ContentValues values;
	
	public DataSource(Context context, String dbName)
	{
		dbHelper = new DatabaseHelper(context, dbName);
		this.values = new ContentValues();
	}
	
	public void open() throws SQLException{
		database = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public void insertWord(String wordSource, String value) throws DictionaryParserException
	{
		values.put(DatabaseHelper.COL_WORDS_SOURCE, wordSource);
		values.put(DatabaseHelper.COL_WORDS_VALUE, value);
		long rowId = database.insert(DatabaseHelper.TABLE_WORDS, null, values);
		values.clear();
		if (rowId == -1)
			throw new DictionaryParserException("Failed to insert word into db");
	}
	
	public void deleteWordById(long wordId)
	{		
		System.out.println("Word deleted with id: " + wordId);
		database.delete(DatabaseHelper.TABLE_WORDS,
		DatabaseHelper.COL_WORDS_ID + " = " + wordId, null);
	}
	
}