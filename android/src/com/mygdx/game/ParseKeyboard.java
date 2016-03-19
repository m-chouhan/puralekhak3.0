package com.mygdx.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.Keyboard.Row;
import android.util.DisplayMetrics;
import android.util.Log;

public class ParseKeyboard extends Keyboard{

	private XmlResourceParser mykeyparser = null;
	private final int VOWEL = 0,DIACRITICS = 1,NUMBER = 3;
	int current_state = VOWEL;
	private int DynamicKeyCount = 0;
	/**Keys to be displayed currently */
	private List<Key> keylist = new ArrayList<Key>();
	/**List of rows */
	private List<Row> rowlist = new ArrayList<Row>();
	
	private List<Key> vowels = new ArrayList<Key>();
	private List<Key> diacritcs = new ArrayList<Key>();
	private List<Key> consonants = new ArrayList<Key>();

	private Key Key_Switch_Mode = null,Key_Delete = null,Switch_Dia = null;
	
	List<Integer> vow = new ArrayList<Integer>();
	List<Integer> dia = new ArrayList<Integer>();
	List<Integer> con = new ArrayList<Integer>();
	
	private List<Integer> digits =new ArrayList<Integer>();	
	public final String TAG = "PARSE";

	
	private int mDisplayWidth;

	private int mDisplayHeight;
	
	public ParseKeyboard(Context context,int dataId,int width,int height) {
		
		super(context, R.xml.empty_hack);
		
		keylist = super.getKeys();
		keylist.clear();
		
        mDisplayWidth = width;
        mDisplayHeight = height;
		mykeyparser = context.getResources().getXml(dataId);	

		try {

			loadKeyboardfromXML(context,mykeyparser);
		} 	catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.d(TAG,"display width"+mDisplayWidth+"display Height"+mDisplayHeight);
        Log.d(TAG,"Parsing Finished");
	}
	
	@Override
    public List<Key> getKeys() {
        return keylist;
    }
	
	void SwitchToDiacritics(int key_code) {
		
		Log.d(TAG,"Changing State to Diacritics Mode");
		current_state = DIACRITICS;
		
		Key key = null;
		int i = 0;
		for(;i< dia.size();++i ) {
			
			key = keylist.get(i);
			String lable = "";
			lable += key_code;
			lable += dia.get(i);
			if(key_code == 0)
				key.label = Character.toString((char)((int)dia.get(i)));
			else 
				key.label = Character.toString((char)key_code) + Character.toString((char)((int)dia.get(i)));
        	key.codes = new int[] { dia.get(i) };
		}
		while(i < DynamicKeyCount ) {
			
			key = keylist.get(i);
			key.label = " ";
        	key.codes = new int[] { 0 };
        	i++;
		}
		
	}
	
	void SwitchToVowels() {

		Log.d(TAG,"Changing State to Vowel Mode");
		current_state = VOWEL;
		
		Key key = null;
		int i = 0;
		for(;i< vow.size();++i ) {
			
			key = keylist.get(i);
			key.label = Character.toString((char)((int)vow.get(i)));
        	key.codes = new int[] { vow.get(i) };
		}
		while(i < DynamicKeyCount) {
			
			key = keylist.get(i);
			key.label = " ";
        	key.codes = new int[] { 0 };
        	i++;
		}
				
	}
	
	/**Handles dynamic keys: returns true if invalidation is needed*/
	public boolean InvalidationNeeded(int code)
	{
		switch(current_state)
		{
			case VOWEL:
				if( con.contains(code)) {
					SwitchToDiacritics(code);
					return true;
				}
				break;
			case DIACRITICS:
				if ( dia.contains(code) ) {
					SwitchToVowels();
					return true;
				}
				break;
			
		}
		return false;
	}
	
	/**Switches between normal and number mode */
	
	public void SwitchMode()
	{
		Log.d(TAG,"Changing State to Diacritics Mode");
		List<Integer> integerList = null;
		
		if( current_state == NUMBER ) {	
			current_state = VOWEL;
			integerList = vow;
			Key_Switch_Mode.label = "[num]";
		}
		
		else {
			current_state = NUMBER;
			integerList = digits;
			Key_Switch_Mode.label = "[vowels]";
		}
		
		Key key = null;
		int i = 0;
		for(;i< integerList.size();++i ) {
			
			key = keylist.get(i);
			key.label = Character.toString((char)((int)integerList.get(i)));
        	key.codes = new int[] { integerList.get(i) };
		}
		while(i < DynamicKeyCount) {
			
			key = keylist.get(i);
			key.label = " ";
        	key.codes = new int[] { 0 };
        	i++;
		}

		return;
	}
	
	private void loadKeyboardfromXML(Context context,XmlResourceParser parser) throws XmlPullParserException, IOException
	{
	        int eventType = parser.getEventType();
	        String currentTag = "";
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	
	        	switch(eventType)
	        	{
		        	case XmlPullParser.START_DOCUMENT:
		                	//Log.d(TAG,"Start document");
		        			break;
		        	case XmlPullParser.START_TAG:
	                		Log.d(TAG,"Start Tag:" + parser.getName());
	                		currentTag = parser.getName();
		        			break;
		        	case XmlPullParser.END_TAG:
		        			Log.d(TAG,"End tag :" + parser.getName());
		        			break;
		        	case XmlPullParser.TEXT:
		                	Log.d(TAG,"Original Text :" + parser.getText());
		                	String currentString = parser.getText().replaceAll("\\s+", "");
	                		List<String> tokens = Arrays.asList(currentString.split(","));
	                		ListIterator<String> it = tokens.listIterator();
		                	
	                		while( it.hasNext()) {
	                			String value = it.next();
	                			if(value.contains("-")) {
	                				String tuple[] = value.split("-");
	                				int start = Integer.parseInt(tuple[0],16),
	                						end = Integer.parseInt(tuple[1],16);
	                		
	                				Log.d(TAG,"["+start+"--"+end+"]");
	                				while(start <= end)
	                				{
	                					if(currentTag.equals("vowels"))
	                						vow.add(start);
	                					else if(currentTag.equals("consonants"))
	                						con.add(start);
	                					else if(currentTag.equals("diacritics"))
	                						dia.add(start);
	                					else if(currentTag.equals("digits"))
	                						digits.add(start);
	                					start++;
	                				}
	                				
	                			}
	                			else {
                					if(currentTag.equals("vowels"))
                						vow.add(Integer.parseInt(value,16));
                					else if(currentTag.equals("consonants"))
                						con.add(Integer.parseInt(value,16));
                					else if(currentTag.equals("diacritics"))
                						dia.add(Integer.parseInt(value,16));                				
                					else if(currentTag.equals("digits"))
                						digits.add(Integer.parseInt(value,16));                				
                				}

	                		}

		                	break;
	        	
	        	}
	        	eventType = parser.next();				
	        }
	        InflateKeysTest();
	}
			
	private void InflateKeysTest()
	{
		List<Integer> bigger = null;
		if( vow.size() < dia.size() ) bigger = dia;
		else bigger = vow;
		
		int totalDKeys = bigger.size();
		DynamicKeyCount = totalDKeys;
		
		int DHeight = mDisplayHeight/4;
		int area = (DHeight*mDisplayWidth)/totalDKeys;
		int keyWidth = (int) Math.sqrt(area),keyHeight;
		keyHeight = keyWidth;
		
		Log.d(TAG,"InflateKeys------");
		Log.d(TAG,"keyDim["+keyHeight+"]"+"area:"+ area + "totalKeys:"+totalDKeys);
		
		int x = 0,y = 0;
		Key key = null;
		Row  R = new Row(this);              
		R.defaultHeight = keyHeight;
		R.defaultWidth = keyWidth;
		R.defaultHorizontalGap = R.verticalGap = 0;
		R.rowEdgeFlags = EDGE_TOP;

		for(int i = 0;i<totalDKeys;++i) {
			//Initialize
			key = new Key(R);
			key.x = x;key.y = y;
			try {
				key.label = Character.toString((char)((int)vow.get(i)));
				key.codes = new int[] { vow.get(i) };
			} catch ( IndexOutOfBoundsException e) {
				key.label = " ";//Character.toString((char)((int)vow.get(i)));
				key.codes = new int[] { 0 };  //0 == null (unicode)
			}
			
        	if(x == 0) key.edgeFlags = Keyboard.EDGE_LEFT;
        	
			keylist.add(key);
			
			//update and check bounds
			x+=keyWidth;
			if(x > (mDisplayWidth-keyWidth) ) {
				
				key.edgeFlags = Keyboard.EDGE_RIGHT;
				rowlist.add(R);
				R = new Row(this);              
				R.defaultHeight = keyHeight; 
				R.defaultWidth = keyWidth;
				R.defaultHorizontalGap = R.verticalGap = 0;
				
				x = 0;y+=keyHeight;
			}
		}
		
		if( x != 0 ) {
			key.edgeFlags = Keyboard.EDGE_RIGHT;
			x = 0;y+=keyHeight;
		}
		
		int ConHeight = mDisplayHeight-DHeight;
		area = (ConHeight*mDisplayWidth)/(con.size()+20);
		keyWidth = keyHeight = (int)Math.sqrt(area);

		R = new Row(this);              
		R.defaultHeight = keyHeight;
		R.defaultWidth = keyWidth;
		R.defaultHorizontalGap = R.verticalGap = 0;

		for(int i = 0;i<con.size();++i) {
			//Initialize
			key = new Key(R);
			key.x = x;key.y = y;
			key.label = Character.toString((char)((long)con.get(i)));
        	key.codes = new int[] { (int)((long)con.get(i)) };
        	
        	if(x == 0) key.edgeFlags = Keyboard.EDGE_LEFT;
        	
			keylist.add(key);
			
			//update and check bounds
			x+=keyWidth;
			if(x > (mDisplayWidth-keyWidth) ) {
				
				key.edgeFlags = Keyboard.EDGE_RIGHT;
				rowlist.add(R);
				R = new Row(this);              
				R.defaultHeight = keyHeight; 
				R.defaultWidth = keyWidth;
				R.defaultHorizontalGap = R.verticalGap = 0;
				x = 0;y+=keyHeight;
			}
		}
		
		if( x != 0 ) {
			key.edgeFlags = Keyboard.EDGE_RIGHT;
			x = 0;y+=keyHeight;
		}
				
		R = new Row(this);              
		R.defaultHeight = keyHeight;
		R.defaultWidth = mDisplayWidth/3;
		R.defaultHorizontalGap = R.verticalGap = 0;
		R.rowEdgeFlags = Keyboard.EDGE_BOTTOM;
		
		Key_Switch_Mode = new Key(R); /*key for switching modes */
		Key_Switch_Mode.x = x;Key_Switch_Mode.y = y;
		Key_Switch_Mode.label = "[num]";
		Key_Switch_Mode.codes = new int[] { Keyboard.KEYCODE_MODE_CHANGE };
		keylist.add(Key_Switch_Mode); 
		
		x+= R.defaultWidth;

		Key_Delete = new Key(R); /*Delete Key */
		Key_Delete.x = x;Key_Delete.y = y;
		Key_Delete.label = "[del]";
		Key_Delete.codes = new int[] { Keyboard.KEYCODE_DELETE};
		keylist.add(Key_Delete);

		x+= R.defaultWidth;

		Switch_Dia = new Key(R);
		Switch_Dia.x = x;Switch_Dia.y = y;
		Switch_Dia.label = "[dia]";
		Switch_Dia.codes = new int[] { Keyboard.KEYCODE_SHIFT};
		keylist.add(Switch_Dia);
		
		rowlist.add(R);
	}
	
}
