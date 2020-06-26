package com.google.moviestvsentiments.usecase.assetList;

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
     * A container that holds metadata about an item view in the asset list.
     */
    static class AssetViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        private AssetViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.asset_card_image);
        }

        /**
         * Loads the provided AssetSentiment's poster image into this AssetViewHolder's ImageView using
         * Glide.
         * @param assetSentiment The AssetSentiment whose poster image should be displayed in this
         *                       AssetViewHolder.
         */
        private void bind(AssetSentiment assetSentiment) {
            Glide.with(imageView).load(assetSentiment.asset().poster()).into(imageView);
        }
    }

    private List<AssetSentiment> assetSentiments;

    private AssetListAdapter() {
        assetSentiments = new ArrayList<>();
    }

    /**
     * Returns a new AssetListAdapter.
     */
    public static AssetListAdapter create() {
        return new AssetListAdapter();
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
        return new AssetViewHolder(itemView);
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
