package com.google.moviestvsentiments.usecase.assetList;

import android.content.Context;
import android.view.View;
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
        RecyclerView movieList = root.findViewById(R.id.movies_list);
        movieList.setAdapter(movieAdapter);
        movieList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,
                false));

        RecyclerView showList = root.findViewById(R.id.tvshows_list);
        showList.setAdapter(showAdapter);
        showList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,
                false));
    }

    /**
     * Sets the displayed list of movies.
     * @param movies The list of movies to display.
     */
    public void setMovies(List<AssetSentiment> movies) {
        movieAdapter.setAssetSentiments(movies);
    }

    /**
     * Sets the displayed list of TV shows.
     * @param shows The list of TV shows to display.
     */
    public void setShows(List<AssetSentiment> shows) {
        showAdapter.setAssetSentiments(shows);
    }
}
