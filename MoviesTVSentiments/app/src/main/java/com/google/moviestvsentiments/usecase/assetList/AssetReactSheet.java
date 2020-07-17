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

/**
 * Displays a BottomSheetDialog that contains details about an Asset and options to change the
 * current user's sentiment toward that Asset.
 */
public class AssetReactSheet {

    private static final String DETAILS_SEPARATOR = " • ";

    private final BottomSheetDialog sheetDialog;
    private final ImageView image;
    private final TextView title;
    private final TextView details;

    private AssetReactSheet(Context context, LayoutInflater inflater) {
        View assetReactSheet = inflater.inflate(R.layout.asset_react_sheet, null);
        image = assetReactSheet.findViewById(R.id.react_sheet_image);
        title = assetReactSheet.findViewById(R.id.react_sheet_title);
        details = assetReactSheet.findViewById(R.id.react_sheet_details);

        sheetDialog = new BottomSheetDialog(context);
        sheetDialog.setContentView(assetReactSheet);
    }

    /**
     * Creates a new AssetReactSheet.
     * @param context The context where the AssetReactSheet is being displayed.
     * @param inflater The LayoutInflater object to use when inflating the AssetReactSheet.
     * @return A new AssetReactSheet.
     */
    public static AssetReactSheet create(Context context, LayoutInflater inflater) {
        return new AssetReactSheet(context, inflater);
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
    }

    /**
     * Displays the AssetReactSheet.
     */
    public void show() {
        sheetDialog.show();
    }
}
