package com.google.moviestvsentiments.usecase.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.service.assetSentiment.AssetSentimentViewModel;
import com.google.moviestvsentiments.usecase.component.AssetListScreen;
import com.google.moviestvsentiments.usecase.signin.SigninActivity;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * A fragment that displays the list of assets that have not been reacted to.
 */
@AndroidEntryPoint
public class HomeFragment extends Fragment {

    @Inject
    AssetSentimentViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.asset_lists_screen, container, false);
        AssetListScreen assetListScreen = AssetListScreen.create(root, container.getContext());

        String accountName = getActivity().getIntent().getStringExtra(SigninActivity.EXTRA_ACCOUNT_NAME);
        viewModel.getAssets(AssetType.MOVIE, accountName, SentimentType.UNSPECIFIED)
            .observe(getViewLifecycleOwner(), movies -> assetListScreen.setMovies(movies));
        viewModel.getAssets(AssetType.SHOW, accountName, SentimentType.UNSPECIFIED)
            .observe(getViewLifecycleOwner(), shows -> assetListScreen.setShows(shows));

        return root;
    }
}