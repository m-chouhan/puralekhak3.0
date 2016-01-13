package com.mygdx.game;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by monty on 8/10/2015.
 */
public class MyKeyboard extends KeyboardView  {

    private final Context ApplicationContext ;
    private ParseKeyboard MyKeyboard;
    private EditText ET = null;

    private String TAG = "CUSTOMKEYBOARD";

    private List<Keyboard.Key> keylist;
    
    private OnKeyboardActionListener KeyBindings = new OnKeyboardActionListener()
    {

        @Override
        public void onPress(int primaryCode) {
        	Log.d(TAG,"OnPress");
        }

        @Override
        public void onRelease(int primaryCode) {
        	Log.d(TAG,"OnRelease");        	
        }

        public void onKey(int primaryCode, int[] keyCodes) {
            //InputConnection ic = getCurrentInputConnection();
            ET = (EditText) ((Activity)ApplicationContext).findViewById(R.id.unicodeedit);
        
            //if( MyKeyboard.HandleKey(primaryCode)) invalidateAllKeys();
            
            switch(primaryCode){
                case Keyboard.KEYCODE_DELETE :
                	
                    int len = ET.getText().length();
                    int deletedcode = 0;
                    if(len > 0)
                    {
                        deletedcode = ET.getText().subSequence(len -1 , len).charAt(0);
                        ET.getText().delete(len- 1,len);
                        //if deleted unicode was a consonant switch to vowel mode
                        if( MyKeyboard.con.contains(deletedcode))
                        	MyKeyboard.SwitchToVowels();
                        else if (MyKeyboard.dia.contains(deletedcode))
                        	MyKeyboard.SwitchToDiacritics(0);
                        invalidateAllKeys();
                    }
                    break;
                case Keyboard.KEYCODE_DONE:
                	break;
                case Keyboard.KEYCODE_MODE_CHANGE:
                	MyKeyboard.SwitchMode();
                	invalidateAllKeys();
                    break;
                case Keyboard.KEYCODE_SHIFT:
                	if(MyKeyboard.current_state == 0 )
                		MyKeyboard.SwitchToDiacritics(0);
                	else if(MyKeyboard.current_state == 1)
                		MyKeyboard.SwitchToVowels();
                	invalidateAllKeys();
                	break;
                default:
                    char code = (char)primaryCode;
                    Log.d(TAG, "Pcode:" + primaryCode);
                    ET.append(Character.toString((char)primaryCode));
                    
                    if( MyKeyboard.InvalidationNeeded(primaryCode) ) {
                    		Log.d(TAG,"Invalidating");
                    		invalidateAllKeys();
                    	}
            }
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {
        	
            Log.d(TAG,"swipeDetected");
        }

        @Override
        public void swipeRight() {
        	Log.d(TAG,"swipeDetected");

        }

        @Override
        public void swipeDown() {
        	Log.d(TAG,"swipeDetected");

        }

        @Override
        public void swipeUp() {
        	Log.d(TAG,"swipeDetected");

        }
    };
    
	private int DataId;

    public MyKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        ApplicationContext = context;                
        ET =  (EditText) ((Activity)ApplicationContext).findViewById(R.id.unicodeedit);                
        if( ET == null ) Log.d(TAG,"ET is null");
        
    }
    
    public MyKeyboard(Context context,int dataId) {
    	super(context,null);
    	ApplicationContext = context;
    	
    	DataId = dataId;
    	
        ET = (EditText) ((Activity)ApplicationContext).findViewById(R.id.unicodeedit);                
        if( ET == null ) Log.d(TAG,"ET is null");

    }
    
    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        
    	super.onSizeChanged(w, h, oldw, oldh);
    	Log.d(TAG,"SizeChangedTo:"+w+"-"+h);
        MyKeyboard = new ParseKeyboard(ApplicationContext, DataId,w,h);
    	setKeyboard(MyKeyboard);
        setOnKeyboardActionListener(KeyBindings);
        keylist = MyKeyboard.getKeys();
    }
    

}
