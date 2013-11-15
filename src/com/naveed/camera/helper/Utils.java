package com.naveed.camera.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
 
public class Utils extends Activity{
 
    private Context _context;
    
 
    // constructor
    public Utils(Context context) {
        this._context = context;
    }
 
    // Reading file paths from SDCard
    public ArrayList<String> getFilePaths(ArrayList<String> paths) {
        ArrayList<String> filePaths = new ArrayList<String>();
        
        for(int i = 0; i < paths.size(); i++){
        	filePaths.add(paths.get(i));
        }
 
		return filePaths;
        
        
    }
     public ArrayList<String> getPictureData(String path){
    	
    	ArrayList<String> pictureData = new ArrayList<String>();
 		SQLiteDatabase db2 = openOrCreateDatabase("PictureDB", 0,
 				null);
         Cursor c2 = db2.rawQuery("SELECT * FROM picData WHERE P_path = '" + path + "'", null);
 		c2.moveToFirst();
 		pictureData.add(c2.getString(c2.getColumnIndex("P_lat")));
 		pictureData.add(c2.getString(c2.getColumnIndex("P_lng")));
 		pictureData.add(c2.getString(c2.getColumnIndex("P_place")));
 		pictureData.add(c2.getString(c2.getColumnIndex("P_description")));
 		pictureData.add(c2.getString(c2.getColumnIndex("P_date")));
 		db2.close();
 		
 		return pictureData;
     }
 

	// Check supported file extensions
    private boolean IsSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());
 
        if (AppConstant.FILE_EXTN
                .contains(ext.toLowerCase(Locale.getDefault())))
            return true;
        else
            return false;
 
    }
 
    /*
     * getting screen width
     */
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
 
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }
}