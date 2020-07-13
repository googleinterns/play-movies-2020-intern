package com.google.moviestvsentiments.service.web;

import androidx.lifecycle.LiveData;
import com.google.moviestvsentiments.model.Account;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * A Retrofit service that provides access to the remote server.
 */
public interface WebService {

    /**
     * Returns a LiveData list of Accounts sorted in ascending alphabetical order.
     */
    @GET("accounts")
    LiveData<ApiResponse<List<Account>>> getAlphabetizedAccounts();

    /**
     * Adds the given list of Accounts to the remote server's database and returns the list of
     * successfully added Accounts.
     * @param accounts The list of Accounts to add.
     * @return The list of successfully added Accounts.
     */
    @POST("accounts")
    LiveData<ApiResponse<Account>> addAccounts(@Body List<Account> accounts);
}
