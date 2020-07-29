package com.google.moviestvsentiments.usecase.assetList;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.model.AssetSentiment;
import java.util.List;

/**
 * A component that displays a list of movies and a list of TV shows.
 */
public class AssetListScreen {

    private AssetListAdapter movieAdapter;
    private AssetListAdapter showAdapter;
    private RecyclerView movieList;
    private RecyclerView showList;
    private TextView movieLabel;
    private TextView showLabel;
    private TextView noAssetsLabel;

    private AssetListScreen(AssetListAdapter.AssetClickListener assetClickListener) {
        movieAdapter = AssetListAdapter.create(assetClickListener);
        showAdapter = AssetListAdapter.create(assetClickListener);
    }

    /**
     * Creates a new AssetListScreen.
     * @param assetClickListener The listener to invoke when an asset is clicked.
     * @param root The root view where the AssetListScreen is displayed. It must contain recycler
     *             views with the movies_list and the tvshows_list ids.
     * @param context The context where the AssetListScreen is displayed.
     * @return A new AssetListScreen.
     */
    public static AssetListScreen create(AssetListAdapter.AssetClickListener assetClickListener,
                                         View root, Context context) {
        AssetListScreen assetListScreen = new AssetListScreen(assetClickListener);
        assetListScreen.setupLists(root, context);
        return assetListScreen;
    }

    private void setupLists(View root, Context context) {
        noAssetsLabel = root.findViewById(R.id.no_assets_label);

        movieLabel = root.findViewById(R.id.movies_label);
        movieList = root.findViewById(R.id.movies_list);
        movieList.setAdapter(movieAdapter);
        movieList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,
                false));

        showLabel = root.findViewById(R.id.tvshows_label);
        showList = root.findViewById(R.id.tvshows_list);
        showList.setAdapter(showAdapter);
        showList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,
                false));
    }

    /**
     * Sets the displayed list of movies.
     * @param movies The list of movies to display.
     */
    public void setMovies(List<AssetSentiment> movies) {
        bind(movies, movieAdapter, movieList, movieLabel, showAdapter);
    }

    /**
     * Sets the displayed list of TV shows.
     * @param shows The list of TV shows to display.
     */
    public void setShows(List<AssetSentiment> shows) {
        bind(shows, showAdapter, showList, showLabel, movieAdapter);
    }

    /**
     * Updates the user interface of the AssetListScreen for a new list of AssetSentiments.
     * @param assetSentiments The new list of AssetSentiments that triggered the update.
     * @param adapter The adapter that should display the list of AssetSentiments.
     * @param list The RecyclerView that displays the adapter.
     * @param label The label associated with the RecyclerView.
     * @param otherAdapter The adapter associated with the list for the other type of Asset.
     */
    private void bind(List<AssetSentiment> assetSentiments, AssetListAdapter adapter,
                      RecyclerView list, TextView label, AssetListAdapter otherAdapter) {
        if (assetSentiments.isEmpty()) {
            list.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
            if (otherAdapter.getItemCount() == 0) {
                noAssetsLabel.setVisibility(View.VISIBLE);
            }
        } else {
            adapter.setAssetSentiments(assetSentiments);
            list.setVisibility(View.VISIBLE);
            label.setVisibility(View.VISIBLE);
            noAssetsLabel.setVisibility(View.GONE);
        }
    }
}
