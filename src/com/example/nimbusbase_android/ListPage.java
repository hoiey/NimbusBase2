package com.example.nimbusbase_android;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;

public class ListPage extends Activity{

	DataBaseAdapter dataBaseAdapter;
	public ListView list = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary_list);
		
		list = (ListView)findViewById(R.id.listView1);
		
//		String[] arr={" 1","2","3"};
//		
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arr);
//		
//		list.setAdapter(adapter);
		
		dataBaseAdapter = new DataBaseAdapter(this);
		
		SQLiteDatabase db =  dataBaseAdapter.getReadableDatabase();
		Cursor cur = db.query(DataBaseAdapter.DB_TABLE,new String[] {DataBaseAdapter.KEY_ID,
				DataBaseAdapter.KEY_TEXT, DataBaseAdapter.KEY_CREAT_TIME, DataBaseAdapter.KEY_TAGS,
				DataBaseAdapter.KEY_GID,DataBaseAdapter.KEY_TIME,DataBaseAdapter.KEY_ID_NIM }, null, null, null, null, null);
		
		if (cur != null && cur.getCount() >= 0){
			
			@SuppressWarnings("deprecation")
			SimpleCursorAdapter	adapter= new SimpleCursorAdapter(ListPage.this,
					R.layout.list_item,
					cur,
					new String[] {DataBaseAdapter.KEY_TEXT, DataBaseAdapter.KEY_CREAT_TIME },
					new int[] { R.id.my_text, R.id.my_time});
	//, DataBaseAdapter.KEY_TAGS,DataBaseAdapter.KEY_GID,DataBaseAdapter.KEY_TIME,DataBaseAdapter.KEY_ID_NIM
			list.setAdapter(adapter);
			}
			
			
		cur.close();
		db.close();
		
		
		
//		if (cur != null && cur.getCount() >= 0)
//		{
//			@SuppressWarnings("deprecation")
//			ListAdapter adapter = new SimpleCursorAdapter(this,
//				android.R.layout.simple_list_item_1,
//				cur,
//				new String[] {DataBaseAdapter.KEY_TEXT, DataBaseAdapter.KEY_CREAT_TIME },
//				new int[] { android.R.id.text1, android.R.id.text1, });
////, DataBaseAdapter.KEY_TAGS,DataBaseAdapter.KEY_GID,DataBaseAdapter.KEY_TIME,DataBaseAdapter.KEY_ID_NIM
//			list.setAdapter(adapter);
//		}
				
	}
	
}
