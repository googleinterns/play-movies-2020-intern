package com.google.moviestvsentiments.usecase.details;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.usecase.assetList.AssetListFragment;

/**
 * An Activity that displays the details of an AssetSentiment object. The AssetSentiment must
 * be passed in the intent that launched the DetailsActivity.
 */
public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

//        Asset asset = Asset.builder().setId("assetId").setType(AssetType.MOVIE).setTitle("assetTitle")
//                .setPoster("posterURL").setBanner("bannerURL").setPlot("plotDescription")
//                .setRuntime("runtime").setYear("year").setTimestamp(1).build();
//        AssetSentiment assetSentiment = AssetSentiment.create(asset, SentimentType.THUMBS_DOWN);
        AssetSentiment assetSentiment = getIntent()
                .getParcelableExtra(AssetListFragment.EXTRA_ASSET_SENTIMENT);
        bind(assetSentiment);
    }

    /**
     * Sets the various views in the DetailsActivity to display the information in the given
     * AssetSentiment.
     * @param assetSentiment The AssetSentiment to display.
     */
    private void bind(AssetSentiment assetSentiment) {
//        ImageView banner = findViewById(R.id.banner_image);
//        Glide.with(this).load(assetSentiment.asset().banner()).into(banner);
//
//        ImageView poster = findViewById(R.id.poster_image);
//        Glide.with(this).load(assetSentiment.asset().poster()).into(poster);

        TextView title = findViewById(R.id.asset_title);
        title.setText(assetSentiment.asset().title());

        TextView rating = findViewById(R.id.rating);
        rating.setText(assetSentiment.asset().imdbRating());

        TextView description = findViewById(R.id.description);
        description.setText(assetSentiment.asset().plot());

        switch (assetSentiment.sentimentType()) {
            case THUMBS_UP:
//                Drawable filledThumbUp = getDrawable(R.drawable.ic_baseline_thumb_up_24);
                ImageView thumbsUp = findViewById(R.id.thumbs_up);
//                thumbsUp.setImageDrawable(filledThumbUp);
                thumbsUp.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                if (matchesSafely(thumbsUp)) {
                    Log.d(TAG, "bind: matches safely returned true");
                } else {
                    Log.d(TAG, "bind: matches safely returned false");
                }
                break;
            case THUMBS_DOWN:
                Drawable filledThumbDown = getDrawable(R.drawable.ic_baseline_thumb_down_24);
                ImageView thumbsDown = findViewById(R.id.thumbs_down);
                thumbsDown.setImageDrawable(filledThumbDown);
                break;
            default:
        }
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
        return bitmap;
    }

    protected boolean matchesSafely(View item) {
        if (!(item instanceof ImageView)) {
            return false;
        }

        ImageView imageView = (ImageView)item;
        Bitmap actualBitmap = getBitmap(imageView.getDrawable());
        Log.d(TAG, "matchesSafely: Image drawable: " + imageView.getDrawable());
        Drawable expectedDrawable = imageView.getContext().getDrawable(R.drawable.ic_outline_thumb_up_24);
        Log.d(TAG, "matchesSafely: Expected drawable " + expectedDrawable);
        Bitmap expectedBitmap = getBitmap(expectedDrawable);

        Log.d(TAG, "matchesSafely: Resource name: " + imageView.getContext().getResources().getResourceEntryName(R.drawable.ic_outline_thumb_up_24));
//        checkBitmaps(actualBitmap, expectedBitmap);
        return expectedBitmap.sameAs(actualBitmap);
    }
}