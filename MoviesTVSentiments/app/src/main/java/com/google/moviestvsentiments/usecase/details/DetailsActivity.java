package com.google.moviestvsentiments.usecase.details;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.usecase.assetList.AssetListFragment;

/**
 * An Activity that displays the details of an AssetSentiment object. The AssetSentiment must
 * be passed in the intent that launched the DetailsActivity.
 */
public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

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
        ImageView banner = findViewById(R.id.banner_image);
        Glide.with(this).load(assetSentiment.asset().banner()).into(banner);

        ImageView poster = findViewById(R.id.poster_image);
        Glide.with(this).load(assetSentiment.asset().poster()).into(poster);

        TextView title = findViewById(R.id.asset_title);
        title.setText(assetSentiment.asset().title());

        TextView rating = findViewById(R.id.rating);
        rating.setText(assetSentiment.asset().imdbRating());

        TextView description = findViewById(R.id.description);
        description.setText(assetSentiment.asset().plot());

        switch (assetSentiment.sentimentType()) {
            case THUMBS_UP:
                Drawable filledThumbUp = getDrawable(R.drawable.ic_baseline_thumb_up_24);
                ImageView thumbsUp = findViewById(R.id.thumbs_up);
                thumbsUp.setImageDrawable(filledThumbUp);
                thumbsUp.setTag(R.drawable.ic_baseline_thumb_up_24);
                break;
            case THUMBS_DOWN:
                Drawable filledThumbDown = getDrawable(R.drawable.ic_baseline_thumb_down_24);
                ImageView thumbsDown = findViewById(R.id.thumbs_down);
                thumbsDown.setImageDrawable(filledThumbDown);
                thumbsDown.setTag(R.drawable.ic_baseline_thumb_down_24);
                break;
            default:
                // by default the ImageViews start with the outlined images, so no action is needed
        }
    }
}