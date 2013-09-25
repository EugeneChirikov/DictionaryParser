package com.mates120.dictionaryparser;

import com.mates120.dictionaryparser.Exceptions.DictionaryParserException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource {
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private ContentValues values;
	
	private String[] allWordsColumns = {
			DatabaseHelper.COL_WORDS_ID,
			DatabaseHelper.COL_WORDS_SOURCE, DatabaseHelper.COL_WORDS_VALUE};
	
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
	
	public long insertWord(String wordSource, String value) throws DictionaryParserException
	{
		values.put(DatabaseHelper.COL_WORDS_SOURCE, wordSource);
		values.put(DatabaseHelper.COL_WORDS_VALUE, value);
		long rowId = database.insert(DatabaseHelper.TABLE_WORDS, null, values);
		values.clear();
		if (rowId == -1)
			throw new DictionaryParserException("Failed to insert word into db");
		return rowId;
	}
	
	public void deleteWordById(long wordId)
	{		
		System.out.println("Word deleted with id: " + wordId);
		database.delete(DatabaseHelper.TABLE_WORDS,
		DatabaseHelper.COL_WORDS_ID + " = " + wordId, null);
	}
	
	public Word getWord(String source)
	{
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
			word.setValue(cursor.getString(2));
		}
		return word;
	}
}