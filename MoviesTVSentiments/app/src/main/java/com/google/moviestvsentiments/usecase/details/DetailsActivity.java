package com.google.moviestvsentiments.usecase.details;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.service.assetSentiment.AssetSentimentViewModel;
import com.google.moviestvsentiments.usecase.assetList.AssetListFragment;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * An Activity that displays the details of an AssetSentiment object. The AssetSentiment must
 * be passed in the intent that launched the DetailsActivity.
 */
@AndroidEntryPoint
public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    @Inject
    AssetSentimentViewModel viewModel;

    private String accountName;
    private AssetSentiment assetSentiment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        accountName = getIntent().getStringExtra(AssetListFragment.EXTRA_ACCOUNT_NAME);
        assetSentiment = getIntent().getParcelableExtra(AssetListFragment.EXTRA_ASSET_SENTIMENT);
        bind();
    }

    /**
     * Sets the various views in the DetailsActivity to display the information in the given
     * AssetSentiment and sets the onClickListener for the thumbs up and thumbs down ImageViews.
     */
    private void bind() {
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

        ImageView thumbsUp = findViewById(R.id.thumbs_up);
        thumbsUp.setOnClickListener(this);

        ImageView thumbsDown = findViewById(R.id.thumbs_down);
        thumbsDown.setOnClickListener(this);

        bindSentimentImages();
    }

    /**
     * Sets the icons displayed by the thumbs up and thumbs down ImageViews.
     */
    private void bindSentimentImages() {
        ImageView thumbsUp = findViewById(R.id.thumbs_up);
        ImageView thumbsDown = findViewById(R.id.thumbs_down);

        switch (assetSentiment.sentimentType()) {
            case THUMBS_UP:
                Drawable filledThumbUp = getDrawable(R.drawable.ic_baseline_thumb_up_24);
                thumbsUp.setImageDrawable(filledThumbUp);
                thumbsUp.setTag(R.drawable.ic_baseline_thumb_up_24);

                Drawable outlineThumbDown = getDrawable(R.drawable.ic_outline_thumb_down_24);
                thumbsDown.setImageDrawable(outlineThumbDown);
                thumbsDown.setTag(R.drawable.ic_outline_thumb_down_24);
                break;
            case THUMBS_DOWN:
                Drawable outlineThumbUp = getDrawable(R.drawable.ic_outline_thumb_up_24);
                thumbsUp.setImageDrawable(outlineThumbUp);
                thumbsUp.setTag(R.drawable.ic_outline_thumb_up_24);

                Drawable filledThumbDown = getDrawable(R.drawable.ic_baseline_thumb_down_24);
                thumbsDown.setImageDrawable(filledThumbDown);
                thumbsDown.setTag(R.drawable.ic_baseline_thumb_down_24);
                break;
            case UNSPECIFIED:
                outlineThumbUp = getDrawable(R.drawable.ic_outline_thumb_up_24);
                thumbsUp.setImageDrawable(outlineThumbUp);
                thumbsUp.setTag(R.drawable.ic_outline_thumb_up_24);

                outlineThumbDown = getDrawable(R.drawable.ic_outline_thumb_down_24);
                thumbsDown.setImageDrawable(outlineThumbDown);
                thumbsDown.setTag(R.drawable.ic_outline_thumb_down_24);
                break;
            default:
                throw new AssertionError("SentimentType has no other possible values");
        }
    }

    /**
     * Handles click events from the thumbs up and thumbs down ImageViews. Updates the sentiment
     * value stored in the database, as well as the assetSentiment instance variable and the icons
     * displayed by the ImageViews.
     * @param view The ImageView that was clicked.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.thumbs_up:
                updateSentiment(SentimentType.THUMBS_UP);
                break;
            case R.id.thumbs_down:
                updateSentiment(SentimentType.THUMBS_DOWN);
                break;
            default:
                throw new AssertionError("Only the thumbs up and thumbs down image " +
                        "views should be clickable");
        }
        bindSentimentImages();
    }

    /**
     * Updates the sentiment value stored in the database, as well as the assetSentiment instance
     * variable, to match the user's new sentiment value. If the clickedSentiment is equal to the
     * current sentiment, then the new sentiment value will be UNSPECIFIED.
     * @param clickedSentiment The SentimentType of the clicked reaction icon.
     */
    private void updateSentiment(SentimentType clickedSentiment) {
        Asset asset = assetSentiment.asset();
        if (assetSentiment.sentimentType() == clickedSentiment) {
            viewModel.updateSentiment(accountName, asset.id(), asset.type(), SentimentType.UNSPECIFIED);
            assetSentiment = AssetSentiment.create(asset, SentimentType.UNSPECIFIED);
        } else {
            viewModel.updateSentiment(accountName, asset.id(), asset.type(), clickedSentiment);
            assetSentiment = AssetSentiment.create(asset, clickedSentiment);
        }
    }
}