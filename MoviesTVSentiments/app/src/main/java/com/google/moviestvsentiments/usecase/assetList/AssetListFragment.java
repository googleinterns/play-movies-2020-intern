package com.google.moviestvsentiments.usecase.assetList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.service.assetSentiment.AssetSentimentViewModel;
import com.google.moviestvsentiments.usecase.details.DetailsActivity;
import com.google.moviestvsentiments.usecase.signin.SigninActivity;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * A fragment that displays a list of assets matching the sentiment type passed in the fragment's
 * arguments.
 */
@AndroidEntryPoint
public class AssetListFragment extends Fragment implements AssetListAdapter.AssetClickListener {

    public static final String EXTRA_ACCOUNT_NAME = "com.google.moviestvsentiments.ACCOUNT_NAME";
    public static final String EXTRA_ASSET_SENTIMENT = "com.google.moviestvsentiments.ASSET_SENTIMENT";

    @Inject
    AssetSentimentViewModel viewModel;

    private String accountName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.asset_lists_screen, container, false);
        AssetListScreen assetListScreen = AssetListScreen.create(this, root,
                container.getContext());

        accountName = getActivity().getIntent().getStringExtra(SigninActivity.EXTRA_ACCOUNT_NAME);
        SentimentType sentimentType = AssetListFragmentArgs.fromBundle(getArguments())
                .getSentimentType();
        viewModel.getAssets(AssetType.MOVIE, accountName, sentimentType)
                .observe(getViewLifecycleOwner(), movies -> assetListScreen.setMovies(movies));
        viewModel.getAssets(AssetType.SHOW, accountName, sentimentType)
                .observe(getViewLifecycleOwner(), shows -> assetListScreen.setShows(shows));

        return root;
    }

    /**
     * Sends an intent containing the current account name and the given AssetSentiment object to
     * the DetailsActivity.
     * @param assetSentiment The AssetSentiment object to pass to the DetailsActivity.
     */
    @Override
    public void onAssetClick(AssetSentiment assetSentiment) {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra(EXTRA_ACCOUNT_NAME, accountName);
        intent.putExtra(EXTRA_ASSET_SENTIMENT, assetSentiment);
        startActivity(intent);
    }
}