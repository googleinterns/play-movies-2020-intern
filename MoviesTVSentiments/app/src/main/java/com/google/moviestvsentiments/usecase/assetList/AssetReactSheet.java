package com.google.moviestvsentiments.usecase.assetList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.SentimentType;

/**
 * Displays a BottomSheetDialog that contains details about an Asset and options to change the
 * current user's sentiment toward that Asset.
 */
public class AssetReactSheet {

    /**
     * Provides callbacks that will be invoked when the AssetReactSheet is clicked.
     */
    public interface ReactSheetClickListener {
        /**
         * Handles either the thumbs up button or the thumbs down button in the AssetReactSheet
         * being clicked.
         * @param assetSentiment The AssetSentiment that the AssetReactSheet was displaying.
         * @param clickedSentiment The SentimentType on the AssetReactSheet that was clicked. This
         *                         will never be UNSPECIFIED.
         */
        void onReactSheetClick(AssetSentiment assetSentiment, SentimentType clickedSentiment);
    }

    private static final String DETAILS_SEPARATOR = " • ";

    private final ReactSheetClickListener clickListener;
    private final Context context;
    private final ImageView image;
    private final TextView title;
    private final TextView details;
    private final ImageView thumbsUpIcon;
    private final ImageView thumbsDownIcon;
    private final View thumbsUpRow;
    private final View thumbsDownRow;
    private final BottomSheetDialog sheetDialog;

    private AssetReactSheet(ReactSheetClickListener clickListener, Context context,
                            LayoutInflater inflater) {
        this.clickListener = clickListener;
        this.context = context;

        View assetReactSheet = inflater.inflate(R.layout.asset_react_sheet, null);
        image = assetReactSheet.findViewById(R.id.react_sheet_image);
        title = assetReactSheet.findViewById(R.id.react_sheet_title);
        details = assetReactSheet.findViewById(R.id.react_sheet_details);
        thumbsUpIcon = assetReactSheet.findViewById(R.id.react_sheet_thumbs_up);
        thumbsDownIcon = assetReactSheet.findViewById(R.id.react_sheet_thumbs_down);
        thumbsUpRow = assetReactSheet.findViewById(R.id.react_sheet_like);
        thumbsDownRow = assetReactSheet.findViewById(R.id.react_sheet_dislike);

        sheetDialog = new BottomSheetDialog(context);
        sheetDialog.setContentView(assetReactSheet);
    }

    /**
     * Creates a new AssetReactSheet.
     * @param clickListener The listener to invoke when the AssetReactSheet is clicked.
     * @param context The context where the AssetReactSheet is being displayed.
     * @param inflater The LayoutInflater object to use when inflating the AssetReactSheet.
     * @return A new AssetReactSheet.
     */
    public static AssetReactSheet create(ReactSheetClickListener clickListener, Context context,
                                         LayoutInflater inflater) {
        return new AssetReactSheet(clickListener, context, inflater);
    }

    /**
     * Updates the details of the AssetReactSheet.
     * @param assetSentiment The new AssetSentiment to display details for.
     * @param posterDrawable The new poster image Drawable to display.
     */
    public void bind(AssetSentiment assetSentiment, Drawable posterDrawable) {
        Asset asset = assetSentiment.asset();
        image.setImageDrawable(posterDrawable);
        title.setText(asset.title());
        details.setText(asset.year() + DETAILS_SEPARATOR + asset.runtime());
        bindReactionIcons(assetSentiment.sentimentType());
        thumbsUpRow.setOnClickListener(view -> react(assetSentiment, SentimentType.THUMBS_UP));
        thumbsDownRow.setOnClickListener(view -> react(assetSentiment, SentimentType.THUMBS_DOWN));
    }

    /**
     * Sets the thumbs up and thumbs down icons in the AssetReactSheet depending on the provided
     * SentimentType. If the SentimentType is THUMBS_UP, the thumbs up icon will be filled in and
     * the thumbs down icon will be outlined. The opposite is true if the SentimentType is
     * THUMBS_DOWN, and both icons will be outlined if the SentimentType is UNSPECIFIED.
     * @param sentimentType The SentimentType to use when setting the icon images.
     */
    private void bindReactionIcons(SentimentType sentimentType) {
        switch (sentimentType) {
            case THUMBS_UP:
                Drawable filledThumbUp = context.getDrawable(R.drawable.ic_baseline_thumb_up_24);
                thumbsUpIcon.setImageDrawable(filledThumbUp);
                thumbsUpIcon.setTag(R.drawable.ic_baseline_thumb_up_24);

                Drawable outlineThumbDown = context.getDrawable(R.drawable.ic_outline_thumb_down_24);
                thumbsDownIcon.setImageDrawable(outlineThumbDown);
                thumbsDownIcon.setTag(R.drawable.ic_outline_thumb_down_24);
                break;
            case THUMBS_DOWN:
                Drawable outlineThumbUp = context.getDrawable(R.drawable.ic_outline_thumb_up_24);
                thumbsUpIcon.setImageDrawable(outlineThumbUp);
                thumbsUpIcon.setTag(R.drawable.ic_outline_thumb_up_24);

                Drawable filledThumbDown = context.getDrawable(R.drawable.ic_baseline_thumb_down_24);
                thumbsDownIcon.setImageDrawable(filledThumbDown);
                thumbsDownIcon.setTag(R.drawable.ic_baseline_thumb_down_24);
                break;
            case UNSPECIFIED:
                outlineThumbUp = context.getDrawable(R.drawable.ic_outline_thumb_up_24);
                thumbsUpIcon.setImageDrawable(outlineThumbUp);
                thumbsUpIcon.setTag(R.drawable.ic_outline_thumb_up_24);

                outlineThumbDown = context.getDrawable(R.drawable.ic_outline_thumb_down_24);
                thumbsDownIcon.setImageDrawable(outlineThumbDown);
                thumbsDownIcon.setTag(R.drawable.ic_outline_thumb_down_24);
                break;
            default:
                throw new AssertionError("SentimentType has no other possible values");
        }
    }

    /**
     * Performs any necessary actions when the AssetReactSheet is clicked.
     * @param assetSentiment The AssetSentiment displayed by the AssetReactSheet.
     * @param clickedSentiment The SentimentType that the user clicked.
     */
    private void react(AssetSentiment assetSentiment, SentimentType clickedSentiment) {
        clickListener.onReactSheetClick(assetSentiment, clickedSentiment);
        sheetDialog.dismiss();
    }

    /**
     * Displays the AssetReactSheet.
     */
    public void show() {
        sheetDialog.show();
    }
}
