package com.example.nimbusbase_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseAdapter  extends SQLiteOpenHelper{
	


	public MetaData metaData;
	
	public static final String	KEY_ID		= "_id";												

	public static final String	KEY_TEXT		= "text";												

	public static final String	KEY_CREAT_TIME		= "create_time";
	
	public static final String	KEY_TAGS		= "tags";
	
	public static final String	KEY_GID		= "gid";
	
	public static final String	KEY_TIME		= "time";
	
	public static final String	KEY_ID_NIM		= "id_nim";

	public static final String	DB_NAME			= "NimbusBase_android.db";

	public static final String	DB_TABLE		= "tweetdiary_Entry";

	public static final int	DB_VERSION		= 1;

	private static final String	DB_CREATE		= "CREATE TABLE " + DB_TABLE + " (" 
												+ KEY_ID + " INTEGER PRIMARY KEY," 
												+ KEY_TEXT + " TEXT,"
												+ KEY_CREAT_TIME + " TEXT,"
												+ KEY_TAGS + " TEXT,"
												+ KEY_GID + " TEXT,"
												+ KEY_TIME + " TEXT,"
												+ KEY_ID_NIM + " TEXT)";

	private SQLiteDatabase		mSQLiteDatabase	= null;

	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(DB_CREATE);
	}
	
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS notes");
		onCreate(db);
	}
	
	DataBaseAdapter(Context context)
	{
		super(context, DB_NAME, null, DB_VERSION);			
	}
	

	public long insertData(MetaData metaData)
	{
		mSQLiteDatabase = this.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TEXT, metaData.getText());
		initialValues.put(KEY_CREAT_TIME, metaData.getCreate_time());
		initialValues.put(KEY_TAGS, metaData.getTags());
		initialValues.put(KEY_GID, metaData.getGid());
		initialValues.put(KEY_TIME, metaData.getTime());
		initialValues.put(KEY_ID_NIM, metaData.getId());		

		long count = mSQLiteDatabase.insert(DB_TABLE, KEY_ID, initialValues);
		
		mSQLiteDatabase.close();
		
		return count;
	}

	public boolean deleteData(String getId)
	{
		mSQLiteDatabase = this.getWritableDatabase();
		boolean b =  mSQLiteDatabase.delete(DB_TABLE, KEY_ID_NIM + "=" + getId, null)>0;
		mSQLiteDatabase.close();
		
		return b;
	}

//	public Cursor fetchAllData()
//	{
//		Cursor mCursor = mSQLiteDatabase.query(DB_TABLE, new String[] { KEY_ID,KEY_TEXT, KEY_CREAT_TIME, KEY_TAGS,KEY_GID,KEY_TIME,KEY_ID_NIM }, null, null, null, null, null);
//		return mCursor;
//	}
//
//	public Cursor fetchData(String getId) throws SQLException
//	{
//		
//		mSQLiteDatabase = this.getWritableDatabase();
//
//		Cursor mCursor =
//
//		mSQLiteDatabase.query(true, DB_TABLE, new String[] { KEY_ID, KEY_TEXT,KEY_CREAT_TIME, KEY_TAGS,KEY_GID,KEY_TIME,KEY_ID_NIM }, KEY_ID_NIM + "=" + getId, null, null, null, null, null);
//
//		if (mCursor != null)
//		{
//			mCursor.moveToFirst();
//		}
//		
//		mSQLiteDatabase.close();
//		
//		return mCursor;
//
//	}

	/* 更新一条数据 */
	public boolean updateData(String getId, MetaData metaData)
	{
		mSQLiteDatabase = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(KEY_TEXT, metaData.getText());
		args.put(KEY_CREAT_TIME, metaData.getCreate_time());
		args.put(KEY_TAGS, metaData.getTags());
		args.put(KEY_GID, metaData.getGid());
		args.put(KEY_TIME, metaData.getTime());
		args.put(KEY_ID_NIM, metaData.getId());	

		boolean b = mSQLiteDatabase.update(DB_TABLE, args, KEY_ID_NIM + "=" + getId, null) > 0;
		
		mSQLiteDatabase.close();
		
		return b;
	}
	
	

}
