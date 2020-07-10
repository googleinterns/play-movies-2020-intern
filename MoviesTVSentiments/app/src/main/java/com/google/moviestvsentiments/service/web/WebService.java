package com.google.moviestvsentiments.service.web;

import androidx.lifecycle.LiveData;
import com.google.moviestvsentiments.model.Account;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface WebService {

    @GET("accounts")
    LiveData<ApiResponse<List<Account>>> getAlphabetizedAccounts();

    @POST("accounts")
    LiveData<ApiResponse<Account>> addAccounts(@Body List<Account> accounts);
}
