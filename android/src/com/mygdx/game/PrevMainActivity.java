package com.mygdx.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.objdetect.HOGDescriptor;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PrevMainActivity extends FragmentActivity{
	
    private static final String TAG = "debug";
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int PICK_FROM_FILE1 = 4; 
    private static final int OPEN_FILE=5;
    private static final int CROP_INPUT=6;
    private static final int CROP_INPUT_FILE=7;
    private static final int CROP_FOR_SPOTTING=8;
    private long fileSize = 0;
	private long fileSize1 = 0;
    private int total_points=0;
    private long difference;
    private String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
    
    private Uri mImageCaptureUri, nImageCaptureUri;
    private AlertDialog dialog,dialog1;
    private Button button, button1, button2, button3, button4;  // select run repeat to text undobuttons
    private ImageView mImageView, iv, res;
    private TextView etime;
    private EditText edittext;
    private ProgressBar progressBarTotal;
    private int totalProgress = 0, totalProgressPre = 0;
    private Mat m,OMatg,TMatg,theImageMat,bw1,fin_img,im_select,tmp,new_mat,tmpPre,im_selectPre;
    private Bitmap bm1, bm2, photo, thumbnail;
	private ProgressDialog progressBar, progressBar1;
	File tf;
	private int progressBarStatus = 0;
	private int progressBarStatus1 = 0;
	private Handler progressBarHandler = new Handler();
	private int selectedCropArea, numberOfMatchings;
	private int opt1;
	private ArrayList<Point> locsPre, locsCurrent;
	private boolean firstSpotting = true, undoToDefault=false, preUndo=false;
	private int ButtonClicked;
	
	private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
	     @Override
	     public void onManagerConnected(int status) {
	       switch (status) {
	           case LoaderCallbackInterface.SUCCESS:
	           {
	              Log.i(TAG, "OpenCV loaded successfully");
	           } break;
	           default:
	           {
	               super.onManagerConnected(status);
	           } break;
	         }
	      }
	 };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        
        Log.i(TAG, "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mOpenCVCallBack)) // Async initialization code
        {
          Log.e(TAG, "Cannot connect to OpenCV Manager");
        }
        else{ Log.i(TAG, "opencv successfull"); 
        }
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
         
        setContentView(R.layout.cv_main);
        
//        setInscriptionTXT();
//        captureImageInitialization(); // function to capture camera
//
//        button = (Button)findViewById(R.id.SelectImageBtn); // select button
//        mImageView = (ImageView)findViewById(R.id.ProfilePicIV); // original image
//        iv = (ImageView) findViewById(R.id.ProfilePicIV1);  // selected template
//        res = (ImageView) findViewById(R.id.ProfilePicIV2);  // result image
//        etime = (TextView)findViewById(R.id.exetime);
//        progressBarTotal = (ProgressBar)findViewById(R.id.progressBarTotal);
//
//        addKeyListener();
//        button.setOnClickListener(new OnClickListener() {
//               @Override
//               public void onClick(View v) {
//                     dialog.show();
//               }
//        });
//        progressBarTotal.setMax(100);
//        button4 = (Button)findViewById(R.id.ButtonUndo);//Undo Spotting
//        button4.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                  undoPreviousSpotting();
//            }
//        });
//        locsPre = new ArrayList<Point>();
//        locsCurrent = new ArrayList<Point>();
//
//        mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
//        LinearLayout.LayoutParams parameters =
//        		new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
//
//        int id = getIntent().getExtras().getInt("RESOURCE_ID", R.xml.keyboard_hindi);
//
//        /*Keyboard view */
//        KeyboardView kv = new MyKeyboard(this,id);
//        kv.setLayoutParams(parameters);
//
//        LinearLayout dragview = (LinearLayout) findViewById(R.id.dragView);
//        dragview.addView(kv);
    }
    
    private void setInscriptionTXT(){
    	File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"inscription.txt");
    	if(f.exists()){
    		Log.d(TAG, "inside inscription.txt exists");
    		f.delete();
    	}
    }
    
    private void captureImageInitialization() {
        /**
         * a selector dialog to display two image source options, from camera
         * �Take from camera� and from existing files �Select from gallery�
         */
        final String[] items = new String[] { "Take from camera","Select from gallery" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int item) { 
                     if (item == 0){   // pick from camera
                    	 /** 
                    	  * To take a photo from camera, pass intent action 
                    	  * �MediaStore.ACTION_IMAGE_CAPTURE� to open the camera app. 
                    	  */
                    	 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                         /**
                          * Also specify the Uri to save the image on specified path
                          * and file name. Note that this Uri variable also used by
                          * gallery app to hold the selected image path.
                          */
                            
                         if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        	 System.out.println("Error");
                         }
                            
                         mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "ori"
                                          //+ String.valueOf(System.currentTimeMillis())
                                          + ".jpg"));

                         intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                         try {
                        	intent.putExtra("return-data", true);
                            startActivityForResult(intent, PICK_FROM_CAMERA);
                         } 
                         catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                         }
                     } else {   // pick from file
                            Intent i = new Intent(Intent.ACTION_PICK,
                            		MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, PICK_FROM_FILE);
                     }
               }
        });
        dialog = builder.create();
    }
//
//    public class CropOptionAdapter extends ArrayAdapter<CropOption> {
//    	private ArrayList<CropOption> mOptions;
//        private LayoutInflater mInflater;
//
//        public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
//               super(context, R.layout.crop_selector, options);
//               mOptions = options;
//               mInflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup group) {
//               if (convertView == null)
//                     convertView = mInflater.inflate(R.layout.crop_selector, null);
//               CropOption item = mOptions.get(position);
//               if (item != null) {
//                     ((ImageView) convertView.findViewById(R.id.iv_icon))
//                                   .setImageDrawable(item.icon);
//                     ((TextView) convertView.findViewById(R.id.tv_name))
//                                   .setText(item.title);
//                     return convertView;
//               }
//               return null;
//        }
//    }
	
	public class CropOption {
        public CharSequence title;
        public Drawable icon;
        public Intent appIntent;
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
        	case OPEN_FILE:
        		Log.d(TAG, "ActivityResult OPEN_FILE");  
        	   	break;
               
        	case PICK_FROM_CAMERA:
        		/**
                 * After taking a picture, do the crop
                 */          	   
        	   File root1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
               File file1 = new File(root1, "ori.jpg");
               m = Highgui.imread(file1.getAbsolutePath());
               if(file1.exists()){
                    System.out.println("Height: " + m.height() + " Width: " + m.width());
               }
               /**
                *  display m in the left imageview
                *  convert to bitmap
                */
               bm1 = Bitmap.createBitmap(m.cols(), m.rows(),Bitmap.Config.ARGB_8888);
               Utils.matToBitmap(m, bm1);
//               mImageView = (ImageView) findViewById(R.id.ProfilePicIV);
               int nh1 = (int) ( bm1.getHeight() * (512.0 / bm1.getWidth()) );
               Bitmap scaled1 = Bitmap.createScaledBitmap(bm1, 512, nh1, true);
               mImageView.setImageBitmap(scaled1);
               
               doCrop(CROP_FOR_SPOTTING);
               break;

        	case PICK_FROM_FILE:
                  /**
                   * After selecting image from files, save the selected path
                   */
        		  Log.d(TAG, "ActivityResult PICK_FROM_FILE");
        		  
                  mImageCaptureUri = data.getData();
        	      if(resultCode==RESULT_OK){
        	    	    String FilePath = data.getData().getPath();
        	    	    Log.d(TAG, "PICK_FROM_FILE filepath "+FilePath);            
        	    	    Uri uri=data.getData();
        	    	    String fileName = null;
        	    	    Context context=getApplicationContext();
        	    	    String scheme = uri.getScheme();
        	    	    if (scheme.equals("file")) {
        	    	    	fileName = uri.getLastPathSegment();
        	    	    }
        	    	    else if (scheme.equals("content")) {
        	    	    	String[] proj = { MediaStore.Video.Media.TITLE };
        	    	    	Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        	    	    	if (cursor != null && cursor.getCount() != 0) {
        	    	    		int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
        	    	    		cursor.moveToFirst();
        	    	    		fileName = cursor.getString(columnIndex);
        	    	    	}
        	    	    }
        	    	    Log.d(TAG, "PICK_FROM_FILE filename "+ fileName);
        	    	    String FilePath2 = mImageCaptureUri.getPath();
        	    	    Log.d(TAG, "PICK_FROM_FILE filepath "+FilePath2);
        	    	    thumbnail = null;
        	    	    Uri picUri = data.getData();//<- get Uri here from data intent
        	    	    if(picUri !=null){
        	    	    	try {
        	    	    		thumbnail = MediaStore.Images.Media.getBitmap(
                                                 this.getContentResolver(), 
                                                 picUri);
        	    	    	} catch (FileNotFoundException e) {
        	    	    		throw new RuntimeException(e);
        	    	    	} catch (IOException e) {
        	    	    		throw new RuntimeException(e);
        	    	    	}
        	    	    }
                    
                  
                  // doing modifications here ends
                  
        	    	    BitmapFactory.Options options = new BitmapFactory.Options();
        	    	    options.inSampleSize = 5;
        	    	    mImageView.setImageBitmap(scaleDownBitmap(thumbnail, 300));
        	    	    m = new Mat();
        	    	    Log.d(TAG, "Before AlertDialog for cropping input");
        	    	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	            builder.setMessage("Do you want to crop input image?")
        	            	.setPositiveButton("Crop", new DialogInterface.OnClickListener() {
        	            		public void onClick(DialogInterface dialog, int id) {
        	            			doCrop(CROP_INPUT_FILE);
        	            		}
        	            	})
        	            	.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        	            		public void onClick(DialogInterface dialog, int id) {
        	            			// User cancelled the dialog
        	            			Utils.bitmapToMat(thumbnail, m );
                    	    	    doCrop(CROP_FOR_SPOTTING);
        	            		}
        	            	});
        	            dialog1 = builder.create();
        	            dialog1.show();
        	      }  
        	     addKeyListener();
                 break;
 
        	case PICK_FROM_FILE1:   
        		Log.d(TAG, "PICK_FROM_FILE1");
        		doCrop(CROP_FOR_SPOTTING);		
        		break;
           
        	case CROP_INPUT:
        		Bundle extras1 = data.getExtras();
                if (extras1 != null) {
                    
                    File root2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File file2 = new File(root2, "croppedInput.jpg");
                    m = Highgui.imread(file2.getAbsolutePath());
                    
                    bm2 = Bitmap.createBitmap(m.cols(), m.rows(),Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(m, bm2);
                    int nh2 = (int) ( bm2.getHeight() * (512.0 / bm2.getWidth()) );
                    Bitmap scaled2 = Bitmap.createScaledBitmap(bm2, 512, nh2, true);
                    mImageView.setImageBitmap(scaled2);
                    mImageCaptureUri=nImageCaptureUri;
                    
                    doCrop(CROP_FOR_SPOTTING);
                  }
        		
                
                break;
        	
        	case CROP_FROM_CAMERA:
                  Bundle extras = data.getExtras();
                  /**
                   * After cropping the image, get the bitmap of the cropped image and
                   * display it on imageview.
                   */
                  Log.d(TAG,"Crop_From_Camera");
                  if (extras != null) {               
                      photo = extras.getParcelable("data");
                      Log.d(TAG, "After Pick Camera and Crop from Camera Option");
                      // convert photo1 to Mat    
                      theImageMat = new Mat();
                      Utils.bitmapToMat(photo, theImageMat ); // converts some Bitmap to some Mat
                      System.out.println("Height: " + theImageMat.height() + " Width: " + theImageMat.width()); 
                      // find the imageview and draw cropped portion in it!
                      int nh = (int) ( photo.getHeight() * (512.0 / photo.getWidth()) );
                      selectedCropArea = photo.getHeight()*photo.getWidth();
                      Bitmap scaled = Bitmap.createScaledBitmap(photo, 512, nh, true);
                      iv.setImageBitmap(scaled);
                      //mSlidingLayout.setPanelState(PanelState.EXPANDED);
                  }
                  // extra ends here
                  addKeyListener();
                  addListenerOnButton();
                  addListenerOnButton1();
                  addListenerOnButton2();
                  addListenerOnButton3();
                  //showUnicodeSelector()
//                  if( ButtonClicked == R.id.button2 )
//                      mSlidingLayout.setPanelState(PanelState.EXPANDED);
                  ButtonClicked = -1;
                  break;
           }
           
    }
	
	public  Bitmap scaleDownBitmap(Bitmap photo, int newHeight) {
		 System.out.println("Inside scaleDownBitmap()");
	     final float densityMultiplier = getResources().getDisplayMetrics().density;
	     int h= (int) (newHeight*densityMultiplier);
	     int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));
	     photo = Bitmap.createScaledBitmap(photo, w, h, true);
	     return photo;
	}

	private void doCrop(int opt) {
		
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        /**
         * Open image crop app by starting an intent
         * �com.android.camera.action.CROP�.
         */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        /**
         *  if there is image cropper app installed.
         */
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        /**
         * If there is no image cropper app, display warning message
         */
        if (size == 0) {
        	Toast.makeText(this, "Can not find image crop app",Toast.LENGTH_SHORT).show();
            return;
        } 
        else {
        	/**
             * Specify the image path, crop dimension and scale
             */
        	intent.setData(mImageCaptureUri);
        	intent.putExtra("scale", true);
        	intent.putExtra("return-data", true);
        	
        	if(opt==CROP_INPUT_FILE){
        		nImageCaptureUri = Uri.fromFile(new File(Environment
            			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "croppedInput.jpg"));
        	}
        	else{
              
        	/* saves the cropped image to the folder */
        	nImageCaptureUri = Uri.fromFile(new File(Environment
        			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "spottingChar.jpg"));
        	}
       		intent.putExtra(MediaStore.EXTRA_OUTPUT,nImageCaptureUri);
            /**
             * There is posibility when more than one image cropper app exist,
             * so we have to  for it first. If there is only one app, open
             * then app.
            */

       		if (size == 1) {
       			Intent i = new Intent(intent);
       			ResolveInfo res = list.get(0);
       			
       			i.setComponent(new ComponentName(res.activityInfo.packageName,
       					res.activityInfo.name));
       			if(opt==CROP_INPUT_FILE){
       				startActivityForResult(i, CROP_INPUT);
       			}
       			else{
       				
       				startActivityForResult(i, CROP_FROM_CAMERA);
       			}
               } 
       		else {
       			/**
       			 * If there are several app exist, create a custom chooser to
       			 * let user selects the app.
       			 */
       			for (ResolveInfo res : list) {
       				final CropOption co = new CropOption();
       				co.title = getPackageManager().getApplicationLabel(
       						res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(
                    		res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName,
                    		res.activityInfo.name));
                    cropOptions.add(co);
       			}

//       			CropOptionAdapter adapter = new CropOptionAdapter(
//       					getApplicationContext(), cropOptions);
       			opt1=opt;
       			AlertDialog.Builder builder = new AlertDialog.Builder(this);
       			if(opt1==CROP_INPUT_FILE){
       				builder.setTitle("Choose Crop Tool");
       			}
       			else{
       				builder.setTitle("Choose Crop Tool for Template Selection....");
       			}
//       			builder.setAdapter(adapter,
//       					new DialogInterface.OnClickListener() {
//       				public void onClick(DialogInterface dialog, int item) {
//       					if(opt1==CROP_INPUT_FILE){
//       						startActivityForResult(
//           							cropOptions.get(item).appIntent,
//           							CROP_INPUT);
//       	       			}
//       	       			else{
//       	       				startActivityForResult(
//       							cropOptions.get(item).appIntent,
//       							CROP_FROM_CAMERA);
//       	       			}
//
//       				}
//       			});
       			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
       				@Override
       				public void onCancel(DialogInterface dialog) {
       					if (mImageCaptureUri != null) {
       						getContentResolver().delete(mImageCaptureUri, null, null);
       						mImageCaptureUri = null;
       					}
       				}
       			});

       			AlertDialog alert = builder.create();
                alert.show();
       		}
        }
	}
	
	public void addListenerOnButton() {
//		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
 
		@Override
		public void onClick(View v) {
			// prepare for a progress bar dialog
			progressBar = new ProgressDialog(v.getContext());
			progressBar.setCancelable(true);
			progressBar.setMessage("Processing please wait ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.setMax(100);
			progressBar.show();
			//reset progress bar status
			progressBarStatus = 0;
			//reset filesize
			fileSize = 0;
			new Thread(new Runnable() {
			  public void run() {
				while (progressBarStatus < 100) {
				  // process some tasks
					progressBarStatus = doSomeTasks();
				  // your computer is too fast, sleep 1 second
				  try {
					Thread.sleep(1000);
				  } catch (InterruptedException e) {
					e.printStackTrace();
				  }
				  /**/
				  // Update the progress bar
				  progressBarHandler.post(new Runnable() {
					  public void run() {
					  progressBar.setProgress(progressBarStatus);
					  }
				  });
				  //*/
				}
 
				// ok, file is downloaded,
				if (progressBarStatus >= 100) {
					// sleep 2 seconds, so that you can see the 100%
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// close the progress bar dialog
					progressBar.dismiss();
					// added 
					addKeyListener();
					setTotalProgressBar();					
					runOnUiThread(new Runnable() {  
	                    @Override
	                    public void run() {
	                    	int nh2 = (int) ( bm2.getHeight() * (512.0 / bm2.getWidth()) );
	                        Bitmap scaled2 = Bitmap.createScaledBitmap(bm2, 512, nh2, true);
	                    	res.setImageBitmap(scaled2);
	                    	etime.setText(String.valueOf((int)difference)+"secs");
	                    }
	                });
					
				}
			  }
			}).start();
			
		}
		
		});
	}

	// Update the progress bar
	public void updateProgressBar(long stat) {
	  
		progressBarStatus = (int)stat;
		progressBarHandler.post(new Runnable() {
			public void run() {
				progressBar.setProgress(progressBarStatus);
			}
		});
	}
	
	public void setTotalProgressBar(){
		Log.d(TAG, "Inside total progress setup");
		Bitmap img = Bitmap.createBitmap(m.cols(), m.rows(),Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(m, img);
		int totalArea = img.getHeight()*img.getWidth();
		Log.d(TAG, "total area="+totalArea+"selected crop area= "+selectedCropArea+"");
		int areaCoveredPercent = (int) ((selectedCropArea*numberOfMatchings*100)/totalArea);
		totalProgressPre = totalProgress;
		totalProgress = totalProgress + areaCoveredPercent;
		Log.d(TAG, "total progress="+totalProgress);
		if(totalProgress<90){
			progressBarTotal.setProgress(totalProgress);
		}
		else{
			progressBarTotal.setProgress(97);
		}
	}

	public void addListenerOnButton1() {
		// repeat button to continue selecting the characters
//		button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
		   
		@Override
		public void onClick(View v) {
		        Intent intent = new Intent();
		        intent.setAction(Intent.ACTION_VIEW);
		        File file1 = new File(PATH, "displaySpotted.jpg");
		        intent.setDataAndType(Uri.fromFile(file1), "image/*");  
//		        ButtonClicked = R.id.button2;
		        doCrop(CROP_FOR_SPOTTING);
           }
		});
	}
		
	public void addListenerOnButton2() {
		//button to generate the text 
//		button3 = (Button) findViewById(R.id.button3);
		button3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v1) {
				// prepare for a progress bar dialog
				progressBar1 = new ProgressDialog(v1.getContext());
				progressBar1.setCancelable(true);
				progressBar1.setMessage("Generating text file.. please wait ...");
				progressBar1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressBar1.setProgress(0);
				progressBar1.setMax(100);
				progressBar1.show();
				//reset progress bar status
				progressBarStatus1 = 0;
				//reset filesize
				fileSize1 = 0;
//				ButtonClicked = R.id.button3;
				
				new Thread(new Runnable() {
				  public void run() {
					while (progressBarStatus1 < 100) {
	 
					  // process some tasks
						progressBarStatus1 = doSomeTasks1();
					  
					  // your computer is too fast, sleep 1 second
					  try {
						Thread.sleep(1000);
					  } catch (InterruptedException e) {
						e.printStackTrace();
					  }
	 
					  // Update the progress bar
					  progressBarHandler.post(new Runnable() {
						public void run() {
						  progressBar1.setProgress(progressBarStatus1);
						}
					  });
					}
	 
					// ok, file is downloaded,
					if (progressBarStatus1 >= 100) {
						// sleep 2 seconds, so that you can see the 100%
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// close the progress bar dialog
						progressBar1.dismiss();
						// added 
		                //System.out.println("Height: " + bw1.height() + " Width: " + bw1.width());
						//res=
						//res.setImageBitmap(finalbmp);
					
						runOnUiThread(new Runnable() {  
		                    @Override
		                    public void run() {
		                    }
		                });
						
					}
				  }
				}).start();	
			}
			
		});
	}
			
	public void addListenerOnButton3() {
//		ImageView imgFavorite = (ImageView) findViewById(R.id.ProfilePicIV2);
//		imgFavorite.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//		        Intent intent = new Intent();
//		        intent.setAction(Intent.ACTION_VIEW);
//		        File file1 = new File(PATH, "displaySpotted.jpg");
//		        intent.setDataAndType(Uri.fromFile(file1), "image/*");
//			}
//		});
	}

	public int doSomeTasks() {
		
		while (fileSize <= 1000000) {
			fileSize++;
			if (fileSize == 100000) {
				return 10;
			} else if (fileSize == 200000) {
				return 20;
			} else if (fileSize == 300000) {
				return 30;
			}
			// ...add your own
			else if (fileSize==400000) {
				fileSize = helloworld(m,theImageMat);
			}
		}
		return 100;
	}
	
	public int doSomeTasks1() {
		while (fileSize1 <= 1000000) {
			fileSize1++;
			if (fileSize1 == 100000) {
				return 10;
			} else if (fileSize1 == 200000) {
				return 20;
			} else if (fileSize1 == 300000) {
				return 30;
			}
			// ...add your own
			else if (fileSize1==400000) {
			fileSize1 = helloworld2();
			}
		}
		return 100;
	}
		
	public long helloworld(Mat OMat,Mat TMat) {
		if(firstSpotting){
			//Do nothing
		}
		else {
			if(locsPre.size()!=0){
				locsCurrent.addAll(locsPre);
			}
			if(!preUndo){
				tmpPre = tmp.clone();
				im_selectPre = im_select.clone();
			}
		}
		double Oheight=0;
		double Owidth=0;
		double Theight=0;
		double Twidth=0;
		
		long lStartTime = new Date().getTime();

        int result_cols =  OMat.cols() - TMat.cols() + 1;
        int result_rows = OMat.rows() - TMat.rows() + 1;
        Mat result= new Mat(result_rows,result_cols,CvType.CV_32FC1);
        OMatg= new Mat(result_rows,result_cols,CvType.CV_8UC1);
        TMatg= new Mat(result_rows,result_cols,CvType.CV_8UC1);
        
        Imgproc.cvtColor(OMat, OMatg, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(TMat, TMatg, Imgproc.COLOR_BGR2GRAY);
        
        System.out.println("Height: " + result.height() + " Width: " + result.width()); 
        
        Oheight=OMatg.rows();
        Owidth=OMatg.cols();
        Theight=TMatg.rows();
        Twidth=TMatg.cols();
        
        //to make matrix dimensions even
        if ((OMatg.rows() % 2)!=0) { Oheight=Oheight+1;}
        if ((OMatg.width() % 2)!=0) { Owidth=Owidth+1;}
        if ((TMatg.rows() % 2)!=0) {Theight=Theight+1;}
        if ((TMatg.width() % 2)!=0) {Twidth=Twidth+1;}
                
        Imgproc.resize(OMatg, OMatg, new Size(Owidth,Oheight), 0, 0, Imgproc.INTER_LINEAR);
        Imgproc.resize(TMatg, TMatg, new Size(Twidth,Theight), 0, 0, Imgproc.INTER_LINEAR);

        Mat roi_grad_x = new Mat();
        Mat roi_grad_y = new Mat();
        Mat image_grad_x = new Mat();
        Mat image_grad_y = new Mat();

        System.out.println("Calculating gradients of roi and original image using sobel\n");
        /// Gradient X of the Original image
        Imgproc.Sobel(OMatg, image_grad_x, CvType.CV_32F, 1, 0, 3,1,0);
        /// Gradient Y of the roi image
        Imgproc.Sobel(OMatg, image_grad_y, CvType.CV_32F, 0, 1, 3,1,0);
        /// Gradient X of the original image
        Imgproc.Sobel(TMatg, roi_grad_x, CvType.CV_32F, 1, 0, 3,1,0);
        /// Gradient Y of the roi image
        Imgproc.Sobel(TMatg, roi_grad_y, CvType.CV_32F, 0, 1, 3,1,0);
        
        Mat image_mag = new Mat();
        Mat roi_mag = new Mat();

        Core.magnitude(image_grad_x, image_grad_y, image_mag);
        Core.magnitude(roi_grad_x, roi_grad_y, roi_mag);

        Mat image_grad = new Mat();
        Mat roi_grad = new Mat();
        Core.convertScaleAbs(image_mag, image_grad);
        Core.convertScaleAbs(roi_mag, roi_grad );
        
        Mat image_mag1 = new Mat();Mat image_mag2 = new Mat();
        image_mag.copyTo( image_mag1 );
        image_mag.copyTo( image_mag2 );
        
        // normalized cross correlation starts..
        
        //cor1 starts
        Mat cf1= new Mat(OMat.rows(),OMat.cols(),CvType.CV_32FC1);
        cf1.setTo(new Scalar(0,0,0,0));

        // Create black image with the same size as the original
        
        Mat roi1 = roi_mag.submat(new Range(0,roi_grad.rows()),new Range(0,roi_grad.cols()/2));
        // Create the result matrix11
        int cor1_cols =  image_mag1.cols() - roi1.cols() + 1;
        int cor1_rows = image_mag1.rows() - roi1.rows() + 1;

        int maxdilate=2;   
        
        Mat cor1 = new Mat(cor1_rows,cor1_cols , CvType.CV_32FC1 );
        Imgproc.matchTemplate(image_mag1, roi1, cor1, Imgproc.TM_CCOEFF_NORMED);
        
        cor1.copyTo(cf1.submat(TMatg.rows()/2, TMatg.rows()/2+cor1.rows()-1, TMatg.cols()/2, cor1.cols()+TMatg.cols()/2-1));
        
        Mat dilatekernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(2 * maxdilate + 1, 2 * maxdilate + 1), new Point(maxdilate, maxdilate));
        Imgproc.dilate(cf1, cf1, dilatekernel, new Point(-1,-1), 5);
        //cor1 ends
        
        fileSize = 100000;
        updateProgressBar(fileSize/10000);
        
        //cor2 begins
        Mat cf2 = new Mat(OMat.rows(),OMat.cols(),CvType.CV_32FC1);
        cf2.setTo(new Scalar(0,0,0,0));

        Mat roi2 = roi_mag.submat(new Range(0,roi_grad.rows()),new Range(roi_grad.cols()/2,roi_grad.cols()));
        /// Create the result matrix11
        int cor2_cols =  image_mag1.cols() - roi2.cols() + 1;
        int cor2_rows = image_mag1.rows() - roi2.rows() + 1;
          
        Mat cor2 = new Mat(cor2_rows,cor2_cols , CvType.CV_32FC1 );

        Imgproc.matchTemplate(image_mag1, roi2, cor2, Imgproc.TM_CCOEFF_NORMED);
       
        for(int i=0; i<cor2.rows(); i++)
        	for(int j=0; j<cor2.cols(); j++)
        		cf2.put(i+TMatg.rows()/2,j,cor2.get(i,j));
     
        Imgproc.dilate(cf2, cf2, dilatekernel, new Point(-1,-1), 5);
        //cor2 ends
        
        fileSize = 200000;
        updateProgressBar(fileSize/10000);

      //cor3 begins
        Mat cf3 = new Mat(OMat.rows(),OMat.cols(),CvType.CV_32FC1);
        cf3.setTo(new Scalar(0,0,0,0));

        Mat roi3 = roi_mag.submat(new Range(0,roi_grad.rows()/2),new Range(0,roi_grad.cols()));
        /// Create the result matrix11
        int cor3_cols =  image_mag1.cols() - roi3.cols() + 1;
        int cor3_rows = image_mag1.rows() - roi3.rows() + 1;
          
        Mat cor3 = new Mat(cor3_cols, cor3_rows, CvType.CV_32FC1 );

        Imgproc.matchTemplate(image_mag1, roi3, cor3, Imgproc.TM_CCOEFF_NORMED);
       
        for(int i=0; i<cor3.rows(); i++)
        	for(int j=0; j<cor3.cols(); j++)
        		cf3.put(i+TMatg.rows()/2-1,j+TMatg.cols()/2-1,cor3.get(i,j));
     
        Imgproc.dilate(cf3, cf3, dilatekernel, new Point(-1,-1), 5);
        //cor3 ends
        
        fileSize = 300000;
        updateProgressBar(fileSize/10000);

        //cor4 begins
        Mat cf4 = new Mat(OMat.rows(),OMat.cols(),CvType.CV_32FC1);
        cf4.setTo(new Scalar(0,0,0,0));

        Mat roi4 = roi_mag.submat(new Range(roi_grad.rows()/2,roi_grad.rows()),new Range(0,roi_grad.cols()));
        /// Create the result matrix11
        int cor4_cols =  image_mag1.cols() - roi4.cols() + 1;
        int cor4_rows = image_mag1.rows() - roi4.rows() + 1;
          
        Mat cor4 = new Mat(cor4_rows,cor4_cols , CvType.CV_32FC1 );

        Imgproc.matchTemplate(image_mag1, roi4, cor4, Imgproc.TM_CCOEFF_NORMED);
       
        cor4.copyTo(cf4.submat(0,cf4.rows()-TMatg.rows()/2+1, TMatg.cols()/2, cf4.cols()-TMatg.cols()/2+1));
        Imgproc.dilate(cf4, cf4, dilatekernel, new Point(-1,-1), 5);
        //cor4 ends
        
        fileSize = 400000;
        updateProgressBar(fileSize/10000);

        //cor5 begins
        Mat cf5 = new Mat(OMat.rows(),OMat.cols(),CvType.CV_32FC1);
        cf5.setTo(new Scalar(0,0,0,0));

        Mat roi5 = roi_mag.submat(new Range(0,roi_grad.rows()/2),new Range(0,roi_grad.cols()/2));
        /// Create the result matrix11
        int cor5_cols =  image_mag1.cols() - roi5.cols() + 1;
        int cor5_rows = image_mag1.rows() - roi5.rows() + 1;
          
        Mat cor5 = new Mat(cor5_rows,cor5_cols , CvType.CV_32FC1 );

        Imgproc.matchTemplate(image_mag1, roi5, cor5, Imgproc.TM_CCOEFF_NORMED);
       
        for(int i=0; i<cor5.rows(); i++)
        	for(int j=0; j<cor5.cols(); j++)
        		cf5.put(i+TMatg.rows()/2-1,j+TMatg.cols()/2-1,cor5.get(i,j));
        
        Imgproc.dilate(cf5, cf5, dilatekernel, new Point(-1,-1), 5);
        //cor5 ends
        
        fileSize = 500000;
        updateProgressBar(fileSize/10000);

        //cor6 begins
        Mat cf6 = new Mat(OMat.rows(),OMat.cols(),CvType.CV_32FC1);
        cf6.setTo(new Scalar(0,0,0,0));

        Mat roi6 = roi_mag.submat(new Range(roi_grad.rows()/2,roi_grad.rows()),new Range(roi_grad.cols()/2,roi_grad.cols()));
        /// Create the result matrix11
        int cor6_cols =  image_mag1.cols() - roi6.cols() + 1;
        int cor6_rows = image_mag1.rows() - roi6.rows() + 1;
          
        Mat cor6 = new Mat(cor6_rows,cor6_cols , CvType.CV_32FC1 );

        Imgproc.matchTemplate(image_mag1, roi6, cor6, Imgproc.TM_CCOEFF_NORMED);
       
        for(int i=0; i<cor6.rows(); i++)
        	for(int j=0; j<cor6.cols(); j++)
        		cf6.put(i,j,cor6.get(i,j));
        
        Imgproc.dilate(cf6, cf6, dilatekernel, new Point(-1,-1), 5);
        //cor6 ends
        
        fileSize = 600000;
        updateProgressBar(fileSize/10000);

        //cor7 begins
        Mat cf7 = new Mat(OMat.rows(),OMat.cols(),CvType.CV_32FC1);
        cf7.setTo(new Scalar(0,0,0,0));
        
        Mat roi7 = roi_mag.submat(new Range(0,roi_grad.rows()/2),new Range(roi_grad.cols()/2,roi_grad.cols()));
        /// Create the result matrix11
        int cor7_cols =  image_mag1.cols() - roi7.cols() + 1;
        int cor7_rows = image_mag1.rows() - roi7.rows() + 1;
          
        Mat cor7 = new Mat(cor7_rows,cor7_cols , CvType.CV_32FC1 );

        Imgproc.matchTemplate(image_mag1, roi7, cor7, Imgproc.TM_CCOEFF_NORMED);
       
        for(int i=0; i<cor7.rows(); i++)
        	for(int j=0; j<cor7.cols(); j++)
        		cf7.put(i+TMatg.rows()/2-1,j,cor7.get(i,j));

        Imgproc.dilate(cf7, cf7, dilatekernel, new Point(-1,-1), 5);
        //cor7 ends
        
        fileSize = 700000;
        updateProgressBar(fileSize/10000);

        //cor8 begins
        Mat cf8 = new Mat(OMat.rows(),OMat.cols(),CvType.CV_32FC1);
        cf8.setTo(new Scalar(0,0,0,0));
        
        Mat roi8 = roi_mag.submat(new Range(roi_grad.rows()/2,roi_grad.rows()),new Range(0,roi_grad.cols()/2));
        /// Create the result matrix11
        int cor8_cols =  image_mag1.cols() - roi8.cols() + 1;
        int cor8_rows = image_mag1.rows() - roi8.rows() + 1;
          
        Mat cor8 = new Mat(cor8_rows,cor8_cols , CvType.CV_32FC1 );

        Imgproc.matchTemplate(image_mag1, roi8, cor8, Imgproc.TM_CCOEFF_NORMED);
       
        for(int i=0; i<cor8.rows(); i++)
        	for(int j=0; j<cor8.cols(); j++)
        		cf8.put(i,j+TMatg.cols()/2-1,cor8.get(i,j));

        Imgproc.dilate(cf8, cf8, dilatekernel, new Point(-1,-1), 5);
        //cor8 ends
        
        fileSize = 800000;
        updateProgressBar(fileSize/10000);

        Mat cf = new Mat( OMat.rows(), OMat.cols(), CvType.CV_32FC1);
        Core.add(cf1, cf2, cf);
        Core.add(cf, cf3, cf);
        Core.add(cf, cf4, cf);
        Core.add(cf, cf5, cf);
        Core.add(cf, cf6, cf);
        Core.add(cf, cf7, cf);
        Core.add(cf, cf8, cf);
        
        Mat bw = new Mat(OMatg.rows(), OMatg.cols(), CvType.CV_32FC1 );
        Imgproc.threshold(cf, bw, 2.3, 1, Imgproc.THRESH_BINARY);

        int erosion_size=2; 

        // set 2nd row to '1'
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(2 * erosion_size + 1, 2 * erosion_size + 1), new Point(erosion_size, erosion_size));
        Imgproc.dilate(bw, bw, kernel, new Point(-1,-1), 2);
        
        Mat bw2 = new Mat();

        bw.convertTo(bw2, CvType.CV_8U);
        // find contours
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(bw2, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        
        //get moments
        List<Moments> mu = new ArrayList<Moments>(contours.size());
        for( int i = 0; i < contours.size(); i++ )
        { 
        	mu.add(i,Imgproc.moments( contours.get(i), false )); 
        }
        //get moment centers
        List<Point> mc = new ArrayList<Point>(contours.size());
        for( int i = 0; i < contours.size(); i++ )
        { 
        	Point p = new Point((int) (mu.get(i).get_m10() / mu.get(i).get_m00()) , (int)(mu.get(i).get_m01()/mu.get(i).get_m00()) );
        	mc.add(i, p);
        }
        
        bw1=new Mat(OMatg.rows(), OMatg.cols(), CvType.CV_8U );
        bw1.setTo(new Scalar(0,0,0,0));
        
        for(int i=0; i<mc.size(); i++){

       	if( ((mc.get(i).x)>0) && ((mc.get(i).y)>0))
       	{
       		bw1.put((int)mc.get(i).y,(int)mc.get(i).x,255);
       		}
        }

        int nz;
        nz = Core.countNonZero(bw1);
        System.out.println("No of points are:"+nz);
        
        List<Point> idx = new ArrayList<Point>(nz);
        int pcntr=0;
        for(  int m = 0; m < bw1.rows(); m++ )
        { 
          for( int n = 0; n < bw1.cols(); n++ )
          {
        	  double[] data = bw1.get(m, n);
        	  if(data[0] > 0)
        	  {   
        		  Point p = new Point(m,n); 
        		  idx.add(pcntr,p); 
        		  pcntr++;   
        	  }
          }
        }
        
        System.out.println("This is "+idx.size());
        for(int i=0;i<idx.size();i++)
        {
        	System.out.println(idx.get(i).x+","+idx.get(i).y);
        }
        
        // Parts based HOG feature matching starts
        Mat score = new Mat(TMatg.rows(), TMatg.cols(), CvType.CV_32FC1);
        //Mat det_imwt = new Mat.zeros(OMatg.rows(),OMatg.cols(),CvType.CV_32FC3);
        Mat det_imwt = Mat.zeros(OMatg.rows(),OMatg.cols(),CvType.CV_32FC3);
        //Vector<Point> locs;
        ArrayList<Point> locs = new ArrayList<Point>();
        for(int i=0;i<idx.size();i++)
        {
        	System.out.println("now for"+i);
        	System.out.println("x is"+idx.get(i).x+"y is"+idx.get(i).y);
        	
          if( (idx.get(i).x>Math.ceil(roi_grad.rows()/2))&&(idx.get(i).x<(image_mag1.rows()-Math.floor(roi_grad.rows()/2)))&&(idx.get(i).y>Math.ceil(roi_grad.cols()/2)) && (idx.get(i).y<(image_mag1.cols()-Math.floor(roi_grad.cols()/2))) )
          {
            Mat temp = OMatg.submat((int)idx.get(i).x-roi_grad.rows()/2,(int)idx.get(i).x+roi_grad.rows()/2,(int)idx.get(i).y-roi_grad.cols()/2,(int)idx.get(i).y+roi_grad.cols()/2);
            score = partialhogmatch4frags(temp,TMatg);
            double[] data1 = score.get(2,2);
            
            System.out.println(data1[0]);
            if (data1[0]>=0.80) 
            {
              System.out.println("inside if");
              det_imwt.submat((int)idx.get(i).x-roi_grad.rows()/2,(int)idx.get(i).x+roi_grad.rows()/2,(int)idx.get(i).y-roi_grad.cols()/2,(int)idx.get(i).y+roi_grad.cols()/2).setTo(new Scalar(data1[0],0,0));
              Point p1 = new Point( idx.get(i).x , idx.get(i).y );
              locs.add(p1);
            }
          }
        }
        locsPre.clear();
        locsPre.addAll(locs);
        if(locsCurrent.size()!=0)
        	locs.addAll(locsCurrent);
       
        numberOfMatchings = locs.size()-locsCurrent.size();
        System.out.println("Points after HOG are "+numberOfMatchings);
        
        //tmp= new Mat( OMatg.rows(), OMatg.cols(), CvType.CV_8UC1 );
        
        if(firstSpotting){
        	tmp = OMatg.clone();
        	im_select = OMatg.clone();
        }
        else{
        	tmp = tmpPre.clone();
        	im_select = im_selectPre.clone();
        }
        
        for(  int m = 0; m < det_imwt.rows(); m++ )
        { 
          for( int n = 0; n < det_imwt.cols(); n++ )
          {
        	  double[] data = det_imwt.get(m, n);
        	  double[] odata = OMatg.get(m,n);
        	  if(data[0] > 0.80)
        	  {   
        		  tmp.put(m, n, data[0]*55) ;
        		  im_select.put(m, n, odata[0]*55);
        	  }
          }
        }
        
        im_select.convertTo(im_select, CvType.CV_8U);
        String filename = "im_select.jpg";
        File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), filename);
        filename = file2.toString();
        Highgui.imwrite(filename, im_select);
        
        List<Mat> ch = new ArrayList<Mat>();
        fin_img = new Mat();
        ch.add(tmp);
        ch.add(OMatg);
        ch.add(OMatg);
        
        Core.merge(ch, fin_img);
        fin_img.convertTo(fin_img, CvType.CV_8U);
        // convert to bitmap:
        bm2 = Bitmap.createBitmap(fin_img.cols(), fin_img.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(fin_img, bm2);
        
        OutputStream outStream = null;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "displaySpotted.jpg");
        try
        {
        	outStream = new FileOutputStream(file);
        	//saving as a JPEG image
        	bm2.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        	outStream.flush();
        	outStream.close();
        	//Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
        }
        catch(FileNotFoundException e)
        {
        	e.printStackTrace();
        }
        catch(IOException e)
        {
        	e.printStackTrace();
        } 
        //
        ArrayList<item>  stuctpoints = new ArrayList<item>();
        
        for(int i=0;i<locs.size();i++)
        {
        	for(  int m = 0; m < det_imwt.rows(); m++ )
            { 
              for( int n = 0; n < det_imwt.cols(); n++ )
              {
            	  double[] data = det_imwt.get(m, n);
            	  if ( ( locs.get(i).x==m )&&( locs.get(i).y==n )&& (data[0]>=0.80))
            	  {
            		  
            		  item it1 = new item();
            		  it1.setx(locs.get(i).x);
            		  it1.sety(locs.get(i).y);
            		  
            		  it1.setposx(TMatg.rows());
            		  it1.setposy(TMatg.cols());
            		  
            		  it1.setScore(data[0]);
            		  stuctpoints.add(it1);
     				  
            	
            	  }
              }
            }
        }
        System.out.println(stuctpoints.size());
        

        // now starting to write to file
        String gettexts = edittext.getText().toString();
        System.out.println(gettexts);
        
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"inscription.txt");
        FileWriter fw;
		try {
			fw = new FileWriter(f,true);
			 BufferedWriter bufwr = new BufferedWriter(fw);
			 bufwr.write("$New$");bufwr.write("\n");
			 bufwr.write(Double.toString(TMatg.rows()));bufwr.write(" ");bufwr.write(Double.toString(TMatg.cols()));bufwr.write(" ");
			 bufwr.write(Double.toString(stuctpoints.size()));bufwr.write(" ");bufwr.write(gettexts);bufwr.write("\n");
			 for(int i=0;i<stuctpoints.size();i++)
	            {
	        		item st = stuctpoints.get(i);
	        		
	        		bufwr.write(Double.toString(st.getx()));bufwr.write(" ");
	        		bufwr.write(Double.toString(st.gety()));bufwr.write(" ");
	        		bufwr.write(Double.toString(st.getScore()));
	        		bufwr.write("\n");
	            }
			 bufwr.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
       
        
		// now starting to write to file ends
		
		long lEndTime = new Date().getTime();
    	difference = Math.round((lEndTime - lStartTime)/1000);
    	System.out.println("Elapsed milliseconds: " + difference);
    	
    	// find the imageview and draw it!
        System.out.println("He He");
        fileSize = 1000000;
        undoToDefault=false;
        if(firstSpotting){
        	firstSpotting = false;
        	undoToDefault = true;
        }
        if(preUndo){
        	preUndo = false;
        }
        runOnUiThread(new Runnable() {
		    public void run() {
		    	button4.setEnabled(true);
		    }
		});
        return fileSize;
    }
	
	public long helloworld2() {
	       
		new_mat = Mat.zeros(OMatg.rows(),OMatg.cols(),CvType.CV_8U);
		ArrayList<item>  filepoints = new ArrayList<item>();
		
		try{
			FileReader fr = new FileReader(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"inscription.txt"));
		    BufferedReader br = new BufferedReader(fr);
		    
		    String line;
		    try {
		    	
				while ((line = br.readLine()) != null) {
					line = br.readLine();
				    // create a new StringReader
				     
				     Scanner sc = new Scanner(line);
				     float roix = sc.nextFloat();
				     float roiy = sc.nextFloat();
				     float no_of_occur = sc.nextFloat();
				     String code = sc.next();
				    // String s2 = sc.next();
				     //int i1 = sc.nextInt();
				   //  if(s1.equalsIgnoreCase("$NEW$"))break;   
				     System.out.print(String.format("%.2f\t%.2f\t%.2f%s\n", roix,roiy,no_of_occur,code));
				     //System.out.print(f1 );//System.out.print("\n" );System.out.print(s1.length() );
				     total_points = total_points+(int) no_of_occur;
				     for(int i=0;i<no_of_occur;i++)
				     {
				    	 
				    	 line = br.readLine();
				    	 Scanner sc1 = new Scanner(line);
					     float px = sc1.nextFloat();
					     float py = sc1.nextFloat();
					     float scr = sc1.nextFloat();
					     for(  int m = 0; m < new_mat.rows(); m++ )
					        { 
					          for( int n = 0; n < new_mat.cols(); n++ )
					          {
					        	  if ( ( px==m )&&( py==n )&& (scr>=0.80))
					        	  {
				        		  
					        		  new_mat.put(m, n, scr*100) ;
					        		  System.out.print(String.format("%.2f %.2f %.2f\n", px,py,scr));
					        		  item it2 = new item();
					           		  it2.setposx(roix);
					           		  it2.setposy(roiy);
					        		  it2.setx(m);
					           		  it2.sety(n);
					        		  it2.setScore(scr);
					        		  it2.setunicode(code);
					           		  filepoints.add(it2);
					           		  	
					        	  }
					          }
					        }
				     }
				     sc.close(); 
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		String ptsimage="points.jpg";
		// now doing the points martix ...to start coding ends
		Highgui.imwrite(ptsimage, new_mat);
		
		// finding the local maximum starts
		int cnt=0;
		int c1,c2,r1,r2;
		double maxVal;
		for( int y = 0; y < new_mat.rows(); y++ )
	   	 { 
			for( int x = 0; x < new_mat.cols(); x++ )
	         	{ 
				
			   if(cnt<total_points)
			   {
				   double[] data2 = new_mat.get(y, x);
	        	  if(data2[0] > 0)
		        	  {
	        		  		c1=x-43;c2=x+43;
	        		  		r1=y-43;r2=y+43;
	        		  		if(x-43<0)
	        		  		{
	        		  			c1=0;
	        		  		}
	        		  		if(y-43<0)
	        		  		{
	        		  			r1=0;
	        		  		}
					
	        		  		if(x+43>new_mat.cols())
	        		  		{
	        		  			c2=x+(x+43-new_mat.cols())-1;
	        		  		}
	        		  		if(y+43>new_mat.rows())
	        		  		{
	        		  			r2=y+(y+43-new_mat.rows())-1;
	        		  		}
					
	        		  		Mat aux = new_mat.submat(r1,r2,c1,c2);
	        		  		
	        		  		maxVal=Core.minMaxLoc(aux).maxVal;
	        		  		if(maxVal>data2[0]){
	        		  			new_mat.put(y, x, 0) ;
	        		  		}
	        		  		System.out.println(data2[0]);System.out.println(maxVal );
				
	        		  		cnt++;
				
		        	  }
				
			   		}	
			
	         	}
			

	   	 	}
		System.out.println(cnt);
		
		// finding the local maximum ends
		//new_mat.convertTo(new_mat, CvType.CV_8U);
		String filename1 = "hehe.jpg";
        File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), filename1);

       filename1 = file1.toString();
       //Imgproc.dilate(new_mat, new_mat, dilatekernel, new Point(-1,-1), 2);
       Highgui.imwrite(filename1, new_mat);
       ArrayList<item>  ptsfinal = new ArrayList<item>();
       
       int indx1=0;
       
       for(  int m = 0; m < new_mat.rows(); m++ )
           { 
             for( int n = 0; n < new_mat.cols(); n++ )
             {
           	  double[] data = new_mat.get(m, n);
           	  if ( data[0]!=0)
           	  {
           		  indx1=0;
           		  while((filepoints.get(indx1).getx()!=m)&&(filepoints.get(indx1).gety()!=n))indx1++;
           		  //item it1 = new item();
           		 // it1.setx(m);
           		 // it1.sety(n);
           		 /// it1.setScore(data[0]);           		
           		  ptsfinal.add(filepoints.get(indx1));         	
           	  }
             }
           }
       
       System.out.println(ptsfinal.size());
       System.out.println("Ye0s");
       for(int i=0;i<ptsfinal.size();i++)
       {
    	   System.out.println(ptsfinal.get(i).getx());System.out.println(ptsfinal.get(i).gety());System.out.println(ptsfinal.get(i).getposx());System.out.println(ptsfinal.get(i).getposy());
    	   System.out.println(ptsfinal.get(i).getunicode());System.out.println("\n");
       }
//       Collections.sort(ptsfinal, new PointCompare());
       System.out.println("Yes");
       for(int i=0;i<ptsfinal.size();i++)
       {
    	   System.out.println(ptsfinal.get(i).getx());System.out.println(ptsfinal.get(i).gety());System.out.println("\n");
       }
       
       int i=0;
       int j=0;
       while(j<ptsfinal.size()-1)
       {
      	 j=j+1;
      	 if((ptsfinal.get(j).getx()-ptsfinal.get(i).getx())<ptsfinal.get(i).getposx()/2){ ptsfinal.get(j).setx(ptsfinal.get(i).getx());   }
      	 else i=j;
      	
       }
//       Collections.sort(ptsfinal, new PointCompare1());
       for(i=0;i<ptsfinal.size();i++)
       {
    	   System.out.println(ptsfinal.get(i).getx());System.out.println(ptsfinal.get(i).gety());System.out.println("\n");
       }
       
       // code to write the unicoode to file starts   
       ListIterator<item> b = ptsfinal.listIterator();
       
       File uf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"outinscription.txt");
       FileWriter ufw;
		try {
			ufw = new FileWriter(uf,true);
			
			 BufferedWriter ubufwr = new BufferedWriter(ufw);

		       String tstr1;
		       String tstr2;
		       
		       while(b.hasNext()) 
		      {
				 	tstr1="\\u";
				 	item ob = b.next();
			      	tstr2=ob. getunicode();
			      	
			      	//tstr1.concat(tstr2);
			      	System.out.println(tstr1+tstr2);
			      	ubufwr.write(tstr1+tstr2);ubufwr.write("\n");
					
					 tstr1="";
				     tstr2="";
			      	
		      }
			 
			 ubufwr.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			try {
				
				tf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"out.txt");
			    FileWriter tfw;
			       
			    FileReader ufr = new FileReader(uf);
			    BufferedReader ubr = new BufferedReader(ufr);
			    
			    tfw = new FileWriter(tf,true);
				
				BufferedWriter tbufwr = new BufferedWriter(tfw);
				 
				System.out.println("Testing");
			    String myString;
			    while ((myString = ubr.readLine()) != null) {
									    
				//String myString = "\u0cb5";
			    	System.out.println(myString);
			       
					String str = myString.split(" ")[0];
					str = str.replace("\\","");
					String[] arr = str.split("u");
					String text1 = "";
					for(int i1 = 1; i1 < arr.length; i1++){
					    int hexVal = Integer.parseInt(arr[i1], 16);
					    text1 += (char)hexVal;
					    tbufwr.write(text1);
					    //System.out.println(text1);
					}
				 
			    }
				 tbufwr.close();
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			File myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"out.txt");
			
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(myFile),getMimeType(myFile.getAbsolutePath()));
			startActivity(intent);

			//startActivity(intent);
			
		/*	
			FileInputStream fIn;
			try {
				fIn = new FileInputStream(tf);
				BufferedReader myReader = new BufferedReader(
    					new InputStreamReader(fIn));
    			String aDataRow = "";
    			String aBuffer = "";
    			while ((aDataRow = myReader.readLine()) != null) {
    				aBuffer += aDataRow + "\n";
    			}
    			txtview.setText(aBuffer);
    			myReader.close();
    			
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
         
       // code to write the unicoode to file ends
	   fileSize1 = 1000000;
       return fileSize1;
    }
	
	private String getMimeType(String url)
    {
        String parts[]=url.split("\\.");
        String extension=parts[parts.length-1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }
	
	Mat partialhogmatch4frags(Mat im1,Mat im2){
		
	  System.out.println("Hog begin");
	  if((im1.rows()!=im2.rows())&&(im1.cols()!=im2.cols()))
		{
			 System.out.println("Image1 and image2 must be of the same size");
		}
	  // initialization of thee matching scores
	  Mat scoreim = new Mat(im1.rows(), im1.cols(), CvType.CV_32FC2);
	  int wins=6;int bins=6;
	  
	  // first part starts
	  Mat pt1 = im1.submat(0,im1.rows()/2,0,im1.cols()/2);
	  Mat pt2 = im2.submat(0,im2.rows()/2,0,im2.cols()/2);
	 
	  System.out.println("Inside Hog function");
	  HOGDescriptor hog = new HOGDescriptor(new Size(16,16),new Size(16,16),new Size(8,8),
              //winSize  //blocksize //blockStride,
			  new Size(8,8), //cellSize,
			  9, //nbins,
			  0, //derivAper,
			  -1, //winSigma,
			  0, //histogramNormType,
			  0.2, //L2HysThresh,
			  true,//gammal correction,
			  64// //nlevels=64);
			  );
	  
	  MatOfFloat hf1 = new MatOfFloat();
	  MatOfFloat hf2 = new MatOfFloat();
	  MatOfPoint loc_hf1 = new MatOfPoint();
	  MatOfPoint loc_hf2 = new MatOfPoint();
	  
	  
	  hog.compute( pt1, hf1, new Size(0,0), new Size(0,0), loc_hf1);
	  
	  Mat HF1 =new Mat(1,1,CvType.CV_32FC1);

	  hog.compute( pt2, hf2, new Size(0,0), new Size(0,0), loc_hf2);
	  
	  Core.divide(hf1, new Scalar(Core.norm(hf1)), hf1, 1.0);
	  Core.divide(hf2, new Scalar(Core.norm(hf2)), hf2, 1.0);
	 
	  Core.transpose(hf1, hf1);
	  Mat z = Mat.ones(hf1.rows(),hf1.cols(), CvType.CV_32FC1);
	  Core.gemm(hf1, hf2, 1.0, new Mat(), 0, HF1, 0);
	  
	  scoreim.submat(0,im1.rows()/2,0,im1.cols()/2).setTo(new Scalar(HF1.get(0,0)));
	  // first part ends

	  // second part starts
	  pt1 = im1.submat(0,im1.rows()/2,im1.cols()/2,im1.cols()); 
	  pt2 = im2.submat(0,im2.rows()/2,im2.cols()/2,im2.cols());
	  
	  hog.compute( pt1, hf1, new Size(0,0), new Size(0,0), loc_hf1);	  
	  hog.compute( pt2, hf2, new Size(0,0), new Size(0,0), loc_hf2);
	  
	  Core.divide(hf1, new Scalar(Core.norm(hf1)), hf1, 1);
	  Core.divide(hf2, new Scalar(Core.norm(hf2)), hf2, 1);
	  
	  Core.transpose(hf1, hf1);
	  Core.gemm(hf1, hf2, 1.0, new Mat(), 0, HF1, 0);
	
	  scoreim.submat(0,im1.rows()/2,im1.cols()/2,im1.cols()).setTo(new Scalar(HF1.get(0,0)));
	  // second part ends


	  // third part starts
	  pt1 = im1.submat(im1.rows()/2,im1.rows(),0,im1.cols()/2); 
	  pt2 = im2.submat(im2.rows()/2,im2.rows(),0,im2.cols()/2);
	  
	  hog.compute( pt1, hf1, new Size(0,0), new Size(0,0), loc_hf1);
	  hog.compute( pt2, hf2, new Size(0,0), new Size(0,0), loc_hf2);
	  
	  Core.divide(hf1, new Scalar(Core.norm(hf1)), hf1, 1);
	  Core.divide(hf2, new Scalar(Core.norm(hf2)), hf2, 1);
	  
	  Core.transpose(hf1, hf1);
	  Core.gemm(hf1, hf2, 1.0, new Mat(), 0, HF1, 0);
	 
	  scoreim.submat(im1.rows()/2,im1.rows(),0,im1.cols()/2).setTo(new Scalar(HF1.get(0,0)));
	  // third part ends


	  // fourth part starts
	  pt1 = im1.submat(im1.rows()/2,im1.rows(),im1.cols()/2,im1.cols()); 
	  pt2 = im2.submat(im2.rows()/2,im2.rows(),im2.cols()/2,im2.cols());
	  hog.compute( pt1, hf1, new Size(0,0), new Size(0,0), loc_hf1);
  
	  hog.compute( pt2, hf2, new Size(0,0), new Size(0,0), loc_hf2);
	  
	  Core.divide(hf1, new Scalar(Core.norm(hf1)), hf1, 1);
	  Core.divide(hf2, new Scalar(Core.norm(hf2)), hf2, 1);
	  
	  Core.transpose(hf1, hf1);
	  Core.gemm(hf1, hf2, 1.0, new Mat(), 0, HF1, 0);	  
	  scoreim.submat(im1.rows()/2,im1.rows(),im1.cols()/2,im1.cols()).setTo(new Scalar(HF1.get(0,0)));
	  // fourth part ends

	  // fifth part starts
	  pt1 = im1.submat(im1.rows()/4,im1.rows()/4+im1.rows()/2,0,im1.cols()/2); 
	  pt2 = im2.submat(im2.rows()/4,im2.rows()/4+im2.rows()/2,0,im2.cols()/2);
	  
	  hog.compute( pt1, hf1, new Size(0,0), new Size(0,0), loc_hf1);
	  hog.compute( pt2, hf2, new Size(0,0), new Size(0,0), loc_hf2);
	  
	  Core.divide(hf1, new Scalar(Core.norm(hf1)), hf1, 1);
	  Core.divide(hf2, new Scalar(Core.norm(hf2)), hf2, 1);
	  
	  Core.transpose(hf1, hf1);
	  Core.gemm(hf1, hf2, 1.0, new Mat(), 0, HF1, 0);
	  scoreim.submat(im1.rows()/4,im1.rows()/4+im1.rows()/2,0,im1.cols()/2).setTo(new Scalar(HF1.get(0,0)));
	  // fifth part ends
	 
	  // sixth part starts
	  pt1 = im1.submat(im1.rows()/4,im1.rows()/4+im1.rows()/2,im1.cols()/2,im1.cols()); 
	  pt2 = im2.submat(im2.rows()/4,im2.rows()/4+im2.rows()/2,im2.cols()/2,im2.cols());
	  
	  hog.compute( pt1, hf1, new Size(0,0), new Size(0,0), loc_hf1);
	  hog.compute( pt2, hf2, new Size(0,0), new Size(0,0), loc_hf2);
	  
	  Core.divide(hf1, new Scalar(Core.norm(hf1)), hf1, 1);
	  Core.divide(hf2, new Scalar(Core.norm(hf2)), hf2, 1);
	  
	  Core.transpose(hf1, hf1);
	  Core.gemm(hf1, hf2, 1.0, new Mat(), 0, HF1, 0);	  
	  scoreim.submat(im1.rows()/4,im1.rows()/4+im1.rows()/2,im1.cols()/2,im1.cols()).setTo(new Scalar(HF1.get(0,0)));
	  // sixth part ends

	  // seventh part starts
	  pt1 = im1.submat(0,im1.rows()/2,im1.cols()/4,im1.cols()/4+im1.cols()/2); 
	  pt2 = im2.submat(0,im2.rows()/2,im2.cols()/4,im2.cols()/4+im2.cols()/2);
	  
	  hog.compute( pt1, hf1, new Size(0,0), new Size(0,0), loc_hf1);
	  hog.compute( pt2, hf2, new Size(0,0), new Size(0,0), loc_hf2);
	  
	  Core.divide(hf1, new Scalar(Core.norm(hf1)), hf1, 1);
	  Core.divide(hf2, new Scalar(Core.norm(hf2)), hf2, 1);
	  
	  Core.transpose(hf1, hf1);
	  Core.gemm(hf1, hf2, 1.0, new Mat(), 0, HF1, 0);	  
	  scoreim.submat(0,im1.rows()/2,im1.cols()/4,im1.cols()/4+im1.cols()/2).setTo(new Scalar(HF1.get(0,0)));
	  // seventh part ends
	  
	  // eight part starts
	  pt1 = im1.submat(im1.rows()/2,im1.rows(),im1.cols()/4,im1.cols()/4+im1.cols()/2); 
	  pt2 = im2.submat(im2.rows()/2,im2.rows(),im2.cols()/4,im2.cols()/4+im2.cols()/2);
	  
	  hog.compute( pt1, hf1, new Size(0,0), new Size(0,0), loc_hf1);
	  hog.compute( pt2, hf2, new Size(0,0), new Size(0,0), loc_hf2);
	  
	  Core.divide(hf1, new Scalar(Core.norm(hf1)), hf1, 1);
	  Core.divide(hf2, new Scalar(Core.norm(hf2)), hf2, 1);
	  
	  Core.transpose(hf1, hf1);
	  Core.gemm(hf1, hf2, 1.0, new Mat(), 0, HF1, 0);
	  scoreim.submat(im1.rows()/2,im1.rows(),im1.cols()/4,im1.cols()/4+im1.cols()/2).setTo(new Scalar(HF1.get(0,0)));
	  // eight part ends
	  Scalar scsum;
	  
	  //Mat rgba( 100, 100, CV_8UC4, Scalar(1,2,3,4) );
	  Mat first = new Mat( scoreim.rows(), scoreim.cols(), CvType.CV_32FC1 );
	  Mat second = new Mat( scoreim.rows(), scoreim.cols(), CvType.CV_32FC1 );
	  // forming array of matrices is quite efficient operations,

	  // because the matrix data is not copied, only the headers
	  // Mat out[] = { first, second };
	  List<Mat> out=new ArrayList<Mat>();
      List<Mat> in=new ArrayList<Mat>();
      out.add(0,first);
      out.add(1,second);
      in.add(0,scoreim);
	  int from_to[] = { 0,0, 1,1 };
	  MatOfInt fromto = new MatOfInt(from_to);
	  
	  //mixChannels( &scoreim, 1, out, 2, from_to, 2 );
	  Core.mixChannels(in, out, fromto);
	  scsum = Core.mean(first);
	  System.out.println(scsum);
	  second.setTo(scsum);
	 
	  System.out.println("Hog end");
	  //cout << "the list meain is  " << scsum << endl;
	  //cout << "the test element in the  image is " << second.at<float>(2,2) << endl;
	  //System.out.println(second.dump());
	  return second;
	  
	}
	
	public Mat maxfilter( Mat m, int win ) {
	  int i1,j1,i2,j2;
	  win=win+1;
	  Mat localmax = Mat.zeros(m.size(), m.type());
	       for( int i = 0; i < m.rows(); i++ ) {
	         for( int j = 0; j < m.cols(); j++ ) {

		   			i1 = maximum(1,i-win);
	       			i2 = minimum(i+win,m.rows());
			        j1 = maximum(1,j-win);
			        j2 = minimum(j+win,m.cols());
				
				//Mat roim = m.submat(j1, j2-j1, i1, i2-i1);
				//Mat roim = m.submat(new Range(i1, i2-i1),new Range( j1, j2-j1));
			        Rect rect = new Rect(j1,i1,j2-j1,i2-i1);
					Mat roim = m.submat(rect);
				//Localizing the best match with minMaxLoc
		        MinMaxLocResult mmr = Core.minMaxLoc(roim);
				localmax.put(i,j,mmr.maxVal);
	         } 
	       }
	  return localmax;
	} 
	
	public int minimum(int x,int y){
		int min;
		if (x<y)min=x; else min=y;
		return min;
	}

	public int maximum(int x,int y){
		int max;
		if (x>y)max=x; else max=y;
		return max;
	}
	
	public void addKeyListener() {
		/*
		edittext = new EditText(this);
		//(EditText) findViewById(R.id.unicodeedit);
		edittext.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// if keydown and "enter" is pressed
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// display a floating message
					Toast.makeText(MainActivity.this,edittext.getText(), Toast.LENGTH_LONG).show();
					return true;
				}
			return false;
		}
	 });*/
	}
	

	public void undoPreviousSpotting(){
		Toast.makeText(this, "Please Wait....!",Toast.LENGTH_SHORT).show();
		if(undoToDefault){
			locsPre.clear();
			locsCurrent.clear();
			firstSpotting = true;
//			iv.setImageResource(R.drawable.titleimg);
//			res.setImageResource(R.drawable.titleimg);
			undoToDefault = false;
			totalProgress = totalProgressPre;
			progressBarTotal.setProgress(totalProgress);
			doCrop(CROP_FOR_SPOTTING);
		}
		else{
			locsPre.clear();
			im_selectPre.convertTo(im_selectPre, CvType.CV_8U);
	        String filename = "im_select.jpg";
	        File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), filename);
	        filename = file2.toString();
	        Highgui.imwrite(filename, im_selectPre);
	        
	        List<Mat> ch = new ArrayList<Mat>();
	        fin_img = new Mat();
	        ch.add(tmpPre);
	        ch.add(OMatg);
	        ch.add(OMatg);
	        
	        Core.merge(ch, fin_img);
	        fin_img.convertTo(fin_img, CvType.CV_8U);
	        // convert to bitmap:
	        bm2 = Bitmap.createBitmap(fin_img.cols(), fin_img.rows(),Bitmap.Config.ARGB_8888);
	        Utils.matToBitmap(fin_img, bm2);
	        
	        OutputStream outStream = null;
	        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "displaySpotted.jpg");
	        try
	        {
	        	outStream = new FileOutputStream(file);
	        	//saving as a JPEG image
	        	bm2.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
	        	outStream.flush();
	        	outStream.close();
	        	//Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
	        }
	        catch(FileNotFoundException e)
	        {
	        	e.printStackTrace();
	        }
	        catch(IOException e)
	        {
	        	
	        	e.printStackTrace();
	        }
			
//			iv.setImageResource(R.drawable.titleimg);
            int nh2 = (int) ( bm2.getHeight() * (512.0 / bm2.getWidth()) );
            Bitmap scaled2 = Bitmap.createScaledBitmap(bm2, 512, nh2, true);
        	res.setImageBitmap(scaled2);
        	totalProgress = totalProgressPre;
			progressBarTotal.setProgress(totalProgress);
			preUndo = true;
		}
		runOnUiThread(new Runnable() {
		    public void run() {
		    	button4.setEnabled(false);
		    }
		});
		
	}
}

