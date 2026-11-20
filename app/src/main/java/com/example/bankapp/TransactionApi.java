package com.example.bankapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TransactionApi {
    @GET("api/transactions/customer/{id}")
    Call<List<TransactionModel>> getTransactions(@Path("id") Long customerId);

    @POST("api/transactions")
    Call<TransactionModel> create(@Body TransactionModel transaction);
}