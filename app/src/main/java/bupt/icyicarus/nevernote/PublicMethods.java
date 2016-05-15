package bupt.icyicarus.nevernote;

import android.graphics.BitmapFactory;

public class PublicMethods {
    public static BitmapFactory.Options getBitmapOption(int size) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = size;
        return options;
    }
}
