package com.mygdx.game;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LanguageSelectionActivity extends Activity implements OnItemClickListener {

	private final String TAG = "LANGUAGE SELECTION";
	private ArrayList<String> Languages = new ArrayList<String>();
	private ArrayList<Integer> ResourceIds = new ArrayList<Integer>();
	private ListView listview ;
	
	@Override
	public void onCreate(Bundle SavedInstanceState)	{
		 
		super.onCreate(SavedInstanceState);
//		setContentView(R.layout.language_selection);
		
        for( Field f: R.xml.class.getDeclaredFields() ) {
        	f.setAccessible(true);
        	try {
        		String str = f.getName();
        		if(str.contains("keyboard")) {
        			Languages.add(f.getName());
        			ResourceIds.add((Integer) f.get(null));
        		}
				Log.d(TAG,f.getName()+":"+f.get(null));
			} catch (IllegalAccessException e) {			
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
        }
//        listview = (ListView) findViewById(R.id.listview);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,Languages);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final String item = (String) parent.getItemAtPosition(position);
		Log.d(TAG,item);
//		Intent intent = new Intent(getBaseContext(), MainActivity.class);
//		intent.putExtra("RESOURCE_ID", ResourceIds.get(position));
//		startActivity(intent);
	}
}
