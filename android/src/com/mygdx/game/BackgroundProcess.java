package com.mygdx.game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.objdetect.HOGDescriptor;

import android.graphics.Bitmap;
import android.os.Environment;
/*Crappy code for image Processing
* TODO: Improve / Clean / */
public class BackgroundProcess {
	
	static private boolean firstSpotting = true, undoToDefault=false, preUndo=false;
	static private Mat m,OMatg,TMatg,theImageMat,bw1,fin_img,im_select,tmp,new_mat,tmpPre,im_selectPre;
	static private int numberOfMatchings;
	static private Bitmap bm1, bm2;
    
	static ArrayList<Point> locsPre = new ArrayList<Point>();
	static ArrayList<Point> locsCurrent = new ArrayList<Point>();
	static private long fileSize = 0;
	static public long helloworld(Mat OMat,Mat TMat) {
		
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
        String gettexts = "\u0905";
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
//    	difference = Math.round((lEndTime - lStartTime)/1000);
//    	System.out.println("Elapsed milliseconds: " + difference);
    	
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
        /*
        runOnUiThread(new Runnable() {
		    public void run() {
		    	button4.setEnabled(true);
		    }
		});*/
        return fileSize;
    }
	
	static private void updateProgressBar(long l) {
		// TODO Auto-generated method stub
		
	}
	static Mat partialhogmatch4frags(Mat im1,Mat im2){
		
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

}/**/
