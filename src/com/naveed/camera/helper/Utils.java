package com.naveed.camera.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
 
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
 
public class Utils{
 
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
 
       //SQLiteDatabase db = openOrCreateDatabase("PictureDB", 0,
		//		null);
       // Cursor c = db.rawQuery("SELECT * FROM data4", null);
		//c.moveToFirst();
		//while(!c.isAfterLast()){
			//String path = c.getString(c.getColumnIndex("P_path"));
			//filePaths.add(path);
			//c.moveToNext();
		//}
		//db.close();
		
		return filePaths;
        
        
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