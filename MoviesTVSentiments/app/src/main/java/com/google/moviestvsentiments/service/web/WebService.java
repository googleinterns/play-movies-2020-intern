package com.google.moviestvsentiments.service.web;

import androidx.lifecycle.LiveData;
import com.google.moviestvsentiments.model.Account;
import java.time.Instant;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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
     * Adds the given name and timestamp to the remote server's Accounts database table and returns
     * the successfully saved Account.
     * @param name The name of the Account to add.
     * @param timestamp The timestamp of the Account to add.
     * @return The successfully saved Account.
     */
    @POST("account")
    LiveData<ApiResponse<Account>> addAccount(@Query("name") String name,
                                              @Query("timestamp") Instant timestamp);

    /**
     * Adds the given list of Accounts to the remote server's database and returns the list of
     * successfully added Accounts.
     * @param accounts The list of Accounts to add.
     * @return The list of successfully added Accounts.
     */
    @POST("accounts")
    LiveData<ApiResponse<List<Account>>> addAccounts(@Body List<Account> accounts);
}
