package com.google.moviestvsentiments.usecase.assetList;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.model.AssetSentiment;
import java.util.ArrayList;
import java.util.List;

/**
 * An adapter that binds a list of AssetSentiment objects to views for display in a RecyclerView.
 * Only the asset poster images are displayed.
 */
public class AssetListAdapter extends RecyclerView.Adapter<AssetListAdapter.AssetViewHolder> {

    /**
     * Provides callbacks to be invoked when an asset is clicked.
     */
    interface AssetClickListener {
        /**
         * Handles an asset in the asset list being clicked.
         * @param assetSentiment An AssetSentiment object containing the clicked asset and the
         *                       current user's sentiment toward that asset.
         */
        void onAssetClick(AssetSentiment assetSentiment);

        /**
         * Handles an Asset in the Asset list being long clicked.
         * @param assetSentiment An AssetSentiment object containing the clicked Asset and the
         *                       current user's sentiment toward that Asset.
         * @param posterDrawable The Drawable of the Asset's poster image.
         */
        void onAssetLongClick(AssetSentiment assetSentiment, Drawable posterDrawable);
    }

    /**
     * A container that holds metadata about an item view in the asset list.
     */
    static class AssetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        private final ImageView imageView;
        private final AssetClickListener assetClickListener;
        private AssetSentiment assetSentiment;

        private AssetViewHolder(@NonNull View itemView, AssetClickListener assetClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            imageView = itemView.findViewById(R.id.asset_card_image);
            this.assetClickListener = assetClickListener;
        }

        /**
         * Loads the provided AssetSentiment's poster image into this AssetViewHolder's ImageView using
         * Glide.
         * @param assetSentiment The AssetSentiment whose poster image should be displayed in this
         *                       AssetViewHolder.
         */
        private void bind(AssetSentiment assetSentiment) {
            this.assetSentiment = assetSentiment;
            Glide.with(imageView).load(assetSentiment.asset().poster()).into(imageView);
        }

        @Override
        public void onClick(View view) {
            assetClickListener.onAssetClick(assetSentiment);
        }

        @Override
        public boolean onLongClick(View view) {
            assetClickListener.onAssetLongClick(assetSentiment, imageView.getDrawable());
            return true;
        }
    }

    private final AssetClickListener assetClickListener;
    private List<AssetSentiment> assetSentiments;

    private AssetListAdapter(AssetClickListener assetClickListener) {
        this.assetClickListener = assetClickListener;
        assetSentiments = new ArrayList<>();
    }

    /**
     * Returns a new AssetListAdapter.
     * @param assetClickListener The listener to invoke when an asset is clicked.
     * @return A new AssetListAdapter.
     */
    public static AssetListAdapter create(AssetClickListener assetClickListener) {
        return new AssetListAdapter(assetClickListener);
    }

    /**
     * Sets the adapter's list of AssetSentiments to a copy of the provided AssetSentiment list.
     * @param assetSentiments The list of AssetSentiments to copy.
     */
    public void setAssetSentiments(List<AssetSentiment> assetSentiments) {
        this.assetSentiments = new ArrayList<>(assetSentiments);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_list_item,
                parent, false);
        return new AssetViewHolder(itemView, assetClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetViewHolder holder, int position) {
        holder.bind(assetSentiments.get(position));
    }

    @Override
    public int getItemCount() {
        return assetSentiments.size();
    }
}
