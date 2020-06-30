package com.google.moviestvsentiments.assertions;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * A TypeSafeMatcher that verifies that an ImageView is displaying a drawable with a given resource
 * id.
 */
public class ImageViewDrawableMatcher extends TypeSafeMatcher<View> {

    private static final String TAG = "ImageViewDrawableMatche";

    private final int drawableId;

    private ImageViewDrawableMatcher(int drawableId) {
        this.drawableId = drawableId;
    }

    /**
     * Returns a new ImageViewDrawableAssertion that checks the image view for the given drawable.
     * @param drawableId The id of the drawable to check for.
     * @return A new ImageViewDrawableAssertion that checks the image view for the given drawable.
     */
    public static ImageViewDrawableMatcher withDrawable(int drawableId) {
        return new ImageViewDrawableMatcher(drawableId);
    }

    /**
     * Returns the Bitmap for the given Drawable.
     * @param drawable The Drawable to get the Bitmap for.
     * @return The Bitmap for the given Drawable.
     */
    private Bitmap getBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        Log.d(TAG, "getBitmap: Bounds: " + drawable.getBounds());
        drawable.draw(canvas);

        Bitmap bmpGrayscale = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bitmap, 0, 0, paint);
        return bmpGrayscale;
//        return bitmap;
    }

    @Override
    protected boolean matchesSafely(View item) {
        if (!(item instanceof ImageView)) {
            return false;
        }

        ImageView imageView = (ImageView)item;
        Bitmap actualBitmap = getBitmap(imageView.getDrawable());
        Log.d(TAG, "matchesSafely: Image drawable: " + imageView.getDrawable());
        Drawable expectedDrawable = imageView.getContext().getDrawable(drawableId);
        Log.d(TAG, "matchesSafely: Expected drawable " + expectedDrawable);
        Bitmap expectedBitmap = getBitmap(expectedDrawable);

        Log.d(TAG, "matchesSafely: Resource name: " + imageView.getContext().getResources().getResourceEntryName(drawableId));
        checkBitmaps(actualBitmap, expectedBitmap);
        return expectedBitmap.sameAs(actualBitmap);
    }

    private void checkBitmaps(Bitmap lhs, Bitmap rhs) {
        if (lhs.getHeight() != rhs.getHeight()) {
            Log.d(TAG, "checkBitmaps: heights are inequal");
        }
        if (lhs.getWidth() != rhs.getWidth()) {
            Log.d(TAG, "checkBitmaps: widths are inequal");
        }

        for (int i = 0; i < lhs.getWidth(); i++) {
            for (int j = 0; j < lhs.getHeight(); j++) {
                if (lhs.getPixel(i, j) != rhs.getPixel(i, j)) {
                    Log.d(TAG, "checkBitmaps: Pixel (" + i + ", " + j + ") is inequal");
                    Log.d(TAG, "checkBitmaps: LHS: " + lhs.getPixel(i, j) + ", RHS: " + rhs.getPixel(i, j));
                    break;
                }
            }
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("ImageView with drawable from resource id: " + drawableId);
    }
}
