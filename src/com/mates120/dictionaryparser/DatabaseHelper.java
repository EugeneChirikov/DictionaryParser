package com.mates120.dictionaryparser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

	public static final String TABLE_WORDS = "words_table";
	public static final String COL_WORDS_ID = "_id";
	public static final String COL_WORDS_SOURCE = "source";
	public static final String COL_WORDS_VALUE = "value";	
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE_WORDS = "create table "
	+ TABLE_WORDS + "(" + COL_WORDS_ID
	+ " integer primary key autoincrement, " + COL_WORDS_SOURCE
	+ " text not null, " + COL_WORDS_VALUE
	+ " text not null);";
	
	public DatabaseHelper(Context context, String dbName)
	{
		super(context, dbName, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database)
	{
		database.execSQL(DATABASE_CREATE_WORDS);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(DatabaseHelper.class.getName(),
			  "Upgrading database from version " + oldVersion + " to "
		      + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
		onCreate(db);
	}
}