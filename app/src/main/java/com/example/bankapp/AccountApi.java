package com.example.bankapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AccountApi {
    @POST("api/accounts")
    Call<AccountModel> create(@Body AccountModel account);

    @GET("api/accounts/customer/{id}")
    Call<AccountModel> getBalance(@Path("id") Long id);

    @PUT("api/accounts/customer/{id}/deposit")
    Call<AccountModel> deposit(@Path("id") Long id, @Query("amount") double amount);

    @PUT("api/accounts/customer/{id}/withdraw")
    Call<AccountModel> withdraw(@Path("id") Long id, @Query("amount") double amount);

    // Zidna hethi mte3 el Virement
    @POST("api/accounts/transfer")
    Call<String> transfer(@Query("fromCustomerId") Long fromId, @Query("toCustomerId") Long toId, @Query("amount") double amount);
}