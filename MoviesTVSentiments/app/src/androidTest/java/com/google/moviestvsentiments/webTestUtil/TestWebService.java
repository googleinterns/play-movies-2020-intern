package com.google.moviestvsentiments.webTestUtil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.moviestvsentiments.model.Account;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.model.UserSentiment;
import com.google.moviestvsentiments.service.web.ApiResponse;
import com.google.moviestvsentiments.service.web.WebService;
import java.time.Instant;
import java.util.List;

/**
 * A mock WebService for use in instrumented tests.
 */
public class TestWebService implements WebService {

    private static final String ERROR = "Using the server in instrumented tests is not supported yet.";

    @Override
    public LiveData<ApiResponse<List<Account>>> getAlphabetizedAccounts() {
        return new MutableLiveData<>(new ApiResponse(new RuntimeException(ERROR)));
    }

    @Override
    public LiveData<ApiResponse<Account>> addAccount(String name, Instant timestamp) {
        return new MutableLiveData<>(new ApiResponse(new RuntimeException(ERROR)));
    }

    @Override
    public ApiResponse<List<Account>> syncPendingAccounts(List<Account> accounts) {
        return new ApiResponse(new RuntimeException(ERROR));
    }

    @Override
    public LiveData<ApiResponse<List<AssetSentiment>>> getAssets(AssetType assetType,
                                                 String accountName, SentimentType sentimentType) {
        return new MutableLiveData<>(new ApiResponse(new RuntimeException(ERROR)));
    }

    @Override
    public LiveData<ApiResponse<UserSentiment>> updateSentiment(String accountName, String assetId,
                            AssetType assetType, SentimentType sentimentType, Instant timestamp) {
        return new MutableLiveData<>(new ApiResponse(new RuntimeException(ERROR)));
    }

    @Override
    public ApiResponse<List<UserSentiment>> syncPendingSentiments(List<UserSentiment> sentiments) {
        return new ApiResponse(new RuntimeException(ERROR));
    }
}
