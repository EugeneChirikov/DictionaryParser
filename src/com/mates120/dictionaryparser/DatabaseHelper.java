package com.mates120.dictionaryparser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	public static final String TABLE_WORDS = "words_table";
	public static final String COL_WORDS_ID = "_id";
	public static final String COL_WORDS_SOURCE = "source";
	public static final String COL_WORDS_SEARCH_COUNTER = "scounter";
	public static final String COL_WORDS_RATING = "rating";
	
	public static final String TABLE_VALUES = "values_table";
	public static final String COL_VALUES_ID = "_id";
	public static final String COL_VALUES_SOURCE = "source";
	public static final String COL_VALUES_DICT_ID = "dict_id";
	
	public static final String TABLE_LINKS = "links_table";
	public static final String COL_LINKS_WORD = "word_id";
	public static final String COL_LINKS_VALUE = "value_id";
	
	public static final String TABLE_DICTIONARIES = "dictionaries_table";
	public static final String COL_DICTIONARIES_ID = "_id";
	public static final String COL_DICTIONARIES_NAME = "dictionary";
	public static final String COL_DICTIONARIES_ISACT = "search_in";
	
	private static final String DATABASE_NAME = "myword.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE_WORDS = "create table "
			+ TABLE_WORDS + "(" + COL_WORDS_ID
			+ " integer primary key autoincrement, " + COL_WORDS_SOURCE
		    + " text not null unique, " + COL_WORDS_SEARCH_COUNTER
		    + " integer not null, " + COL_WORDS_RATING
		    + " integer not null);";
	
	private static final String DATABASE_CREATE_VALUES = "create table "
			+ TABLE_VALUES + " (" + COL_VALUES_ID
			+ " integer primary key autoincrement, " + COL_VALUES_SOURCE 
			+ " text not null, " + COL_VALUES_DICT_ID
			+ " integer not null, "
			+ " foreign key (" + COL_VALUES_DICT_ID + ") references " 
			+ TABLE_DICTIONARIES + "(" + COL_DICTIONARIES_ID + ") on delete cascade);";
	
	private static final String DATABASE_CREATE_LINKS = "create table "
			+ TABLE_LINKS + " (" + COL_LINKS_WORD 
			+ " integer not null, " 
			+ COL_LINKS_VALUE + " integer not null,"
			+ " primary key (" + COL_LINKS_WORD + ", " + COL_LINKS_VALUE + "), " 
			+ "foreign key (" + COL_LINKS_WORD + ") references "+ TABLE_WORDS +"(" 
			+ COL_WORDS_ID + ") on delete cascade," 
			+ "foreign key (" + COL_LINKS_VALUE + ") references " +  TABLE_VALUES + "(" 
			+ COL_VALUES_ID + ") on delete cascade "
			+ ");";
	
	private static final String DATABASE_CREATE_DICTIONARIES = "create table "
			+ TABLE_DICTIONARIES + " (" + COL_DICTIONARIES_ID
			+ " integer primary key autoincrement, "
			+ COL_DICTIONARIES_NAME + " text not null unique, "
			+ COL_DICTIONARIES_ISACT + " integer not null"
			+ ");";
	
	public DatabaseHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE_WORDS);
	    database.execSQL(DATABASE_CREATE_VALUES);
	    database.execSQL(DATABASE_CREATE_LINKS);
	    database.execSQL(DATABASE_CREATE_DICTIONARIES);
	  }
	  
	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(DatabaseHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_VALUES);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINKS);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_DICTIONARIES);
	    onCreate(db);
	  }
}
