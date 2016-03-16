package byr.criminalintent.javabean;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

/**
 * Created by L.Y.C on 2016/3/14.
 */
public class PictureUtils {
    private static final String TAG = "PictureUtils";

    /**
     * 从本地文件获取一个缩小的适合目前window尺寸的BitmapDrawable
     */
    public static BitmapDrawable getScaledDrawable(Activity a, String path) {
        Log.e(TAG, "cleanImageView");
        Display display = a.getWindowManager().getDefaultDisplay();
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        //读取图片的尺寸
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        Log.e(TAG, "inSampleSize " + inSampleSize);
        return new BitmapDrawable(a.getResources(), bitmap);
    }

    /**
     * 卸载图片节约内存
     * 虽然finalizer可以清理，但可能清理前就出现内存耗尽
     * so主动调用Bitmap.recycle()
     */
    public static void cleanImageView(ImageView imageView) {
        Log.e(TAG, "cleanImageView");
        if (!(imageView.getDrawable() instanceof BitmapDrawable)) {
            return;
        }
        BitmapDrawable b = (BitmapDrawable) imageView.getDrawable();
        b.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }
}
