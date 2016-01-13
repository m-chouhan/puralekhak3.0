package com.mygdx.game;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.opencv.android.CameraBridgeViewBase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.SurfaceView;

/* CC:F7:26:3A:00:3E:36:50:B5:3A:B5:40:EE:DC:80:21:68:15:1E:94
 * API_KEY:AIzaSyDjmrjBEPvgPlXtT18BQgl9GOYR3vzf304
 * */

public class FragmentView extends Fragment {
    // Store instance variables
    private int ID;
    private final String TAG = "FRAGEMENTVIEW";
    private final String POST_ADDR = "http://10.5.23.220/donor.php";
    private final String GET_ADDR = "http://10.5.23.220/bookrec.php"; 
    
 
    public static FragmentView newInstance(int id) {
        FragmentView fragmentFirst = new FragmentView();
        Bundle args = new Bundle();
        args.putInt("someInt", id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ID = getArguments().getInt("someInt", 1);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
    	
        View view = null;
        switch(ID) {
	        case 0:
	    			view = inflater.inflate(R.layout.donor_view, container, false);
	    			ImageButton I = (ImageButton) view.findViewById(R.id.submit_location);
	    			
	    			I.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							HandlePost(getView());
						}
					});
	    			break;
	        case 2:
					view = inflater.inflate(R.layout.keyboard_view, container, false);
					break;
	        case 1:
					view = inflater.inflate(R.layout.cv_main, container, false);

                    CameraBridgeViewBase mOpenCvCameraView = (CameraBridgeViewBase) view.findViewById(R.id.HelloOpenCvView);
					mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
					//mOpenCvCameraView.setCvCameraViewListener(this);
					mOpenCvCameraView.enableView();
	        		break;
        }

        Spinner s = (Spinner) view.findViewById(R.id.item_category);
		if ( s != null) {
			
	        String[] items = new String[] { "Books", "Vehicle", "Furniture" };
			 
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
	                android.R.layout.simple_spinner_item, items);
	        s.setAdapter(adapter);
		}
		        
		return view;
    }

	protected void HandleGet(View view) {
		
		Log.d(TAG,"HandleGet");
		
		Spinner s = (Spinner) view.findViewById(R.id.item_category);
		EditText edit_name = (EditText) view.findViewById(R.id.edit_name);		
		EditText sub_cat = (EditText)view.findViewById(R.id.edit_sub_category);
		EditText author = (EditText)view.findViewById(R.id.edit_author);
		EditText tags = (EditText)view.findViewById(R.id.edit_tags);
		
		Log.d(TAG,edit_name.getText().toString());
		Log.d(TAG,s.getSelectedItem().toString());
		
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        
        nameValuePair.add(new BasicNameValuePair("item_category", s.getSelectedItem().toString()) );
        nameValuePair.add(new BasicNameValuePair("name", edit_name.getText().toString()));
        nameValuePair.add(new BasicNameValuePair("sub_category", sub_cat.getText().toString()));
        nameValuePair.add(new BasicNameValuePair("book_author", author.getText().toString()));
        nameValuePair.add(new BasicNameValuePair("tags", tags.getText().toString()));
        
        Thread t = new Thread( new WorkerThreadGet(GET_ADDR,nameValuePair) );
        t.start();
    }

	protected void HandlePost(View view) {
		
		Log.d(TAG,"HandlePost");

		Spinner s = (Spinner) view.findViewById(R.id.item_category);

		EditText edit_name = (EditText) view.findViewById(R.id.edit_name);
		
		Log.d(TAG,edit_name.getText().toString());
		Log.d(TAG,s.getSelectedItem().toString());
		
		EditText sub_cat = (EditText)view.findViewById(R.id.edit_sub_category);
		EditText author = (EditText)view.findViewById(R.id.edit_author);
		EditText number = (EditText)view.findViewById(R.id.edit_number);
		EditText tags = (EditText)view.findViewById(R.id.edit_tags);
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        
        nameValuePair.add(new BasicNameValuePair("item_category", s.getSelectedItem().toString()) );
        nameValuePair.add(new BasicNameValuePair("name", edit_name.getText().toString()));
        nameValuePair.add(new BasicNameValuePair("sub_category", sub_cat.getText().toString()));
        nameValuePair.add(new BasicNameValuePair("book_author", author.getText().toString()));
        nameValuePair.add(new BasicNameValuePair("contact", number.getText().toString()));
        nameValuePair.add(new BasicNameValuePair("tags", tags.getText().toString()));
        
        Thread t = new Thread( new WorkerThreadPost(POST_ADDR,nameValuePair) );
        t.start();
        
	}
	
	private class WorkerThreadPost implements Runnable {
		
		private String string;
		private List<NameValuePair> nameValuePair;
		
		public WorkerThreadPost(String str, List<NameValuePair> nameValue) {
			this.nameValuePair = nameValue;
			this.string = str;
		}
		
		@Override
		public void run() {
			
	        HttpClient httpClient = new DefaultHttpClient();
	        
			HttpPost httpPost = new HttpPost(string); 
			
			//Encoding POST data
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			//making POST request.
			try {
				HttpResponse response = httpClient.execute(httpPost);
				String responseStr = EntityUtils.toString(response.getEntity());
				// write response to log
				Log.d("Http Post Response:", responseStr);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}};
		
		private class WorkerThreadGet implements Runnable {
			
			private String string;
			private List<NameValuePair> nameValuePair;
			
			public WorkerThreadGet(String str, List<NameValuePair> nameValue) {
				this.nameValuePair = nameValue;
				this.string = str;
			}
			
			@Override
			public void run() {
				
		        HttpClient httpClient = new DefaultHttpClient();
		        
				HttpPost httpPost = new HttpPost(string); 
				
				//Encoding POST data
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				//making POST request.
				try {
					HttpResponse response = httpClient.execute(httpPost);
					String responseStr = EntityUtils.toString(response.getEntity());
					// write response to log
					Log.d("Http Post Response:", responseStr);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}};
		
}