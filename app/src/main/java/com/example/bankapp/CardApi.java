package com.example.bankapp;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CardApi {
    @GET("api/cards/customer/{customerId}")
    Call<List<CardModel>> getCards(@Path("customerId") Long customerId);

    @POST("api/cards")
    Call<CardModel> createCard(@Body CardModel card);
}