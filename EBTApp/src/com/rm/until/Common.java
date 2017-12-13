package com.rm.until;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;

public class Common {
	
	
	public static void ysImg(String imagePath)
	{
		//2048x1536	
		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeFile(imagePath);
		Matrix matrix = new Matrix();
		float width=1536;
		float height=2048;
		if(bitmap.getWidth()>bitmap.getHeight())
		{
			width=2048;
			height=1536;
		}
		if(width>bitmap.getWidth())
			width=bitmap.getWidth();
		if(height>bitmap.getHeight())
			height=bitmap.getHeight();
        matrix.setScale(width/bitmap.getWidth(), height/bitmap.getHeight());
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
        		bitmap.getHeight(), matrix, true);
		saveImg(imagePath,bitmap);
	}
	
	private static void saveImg(String path,Bitmap bitmap)
	{
	    try {  
            File file = new File(path);  
            FileOutputStream out = new FileOutputStream(file);  
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);  
            out.flush();  
            out.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
		
	public static Bitmap getImageThumbnail(String imagePath, int coefficient) {
		try {
			Bitmap bitmap = null;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeFile(imagePath, options);
			options.inJustDecodeBounds = false;
			int h = options.outHeight;
			int w = options.outWidth;
			int width = w / coefficient;
			int height = h / coefficient;
			options.inSampleSize = coefficient;
			bitmap = BitmapFactory.decodeFile(imagePath, options);
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
			return bitmap;
		} catch (Exception e) {
			return null;
		}
	}
	
	/** 
     * 判断某个服务是否正在运行的方法 
     *  
     * @param mContext 
     * @param serviceName 
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService） 
     * @return true代表正在运行，false代表服务没有正在运行 
     */  
    public static boolean isServiceWork(Context mContext, String serviceName) {  
        boolean isWork = false;  
        ActivityManager myAM = (ActivityManager) mContext  
                .getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningServiceInfo> myList = myAM.getRunningServices(40);  
        if (myList.size() <= 0) {  
            return false;  
        }  
        for (int i = 0; i < myList.size(); i++) {  
            String mName = myList.get(i).service.getClassName().toString();  
            if (mName.equals(serviceName)) {  
                isWork = true;  
                break;  
            }  
        }  
        return isWork;  
    }
}
