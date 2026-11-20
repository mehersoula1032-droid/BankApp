package com.example.bankapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;        // ✅ زدنا هذي
import retrofit2.http.Path;
import retrofit2.http.Query;      // ✅ زدنا هذي

public interface CustomerApi {

    @POST("api/customers")
    Call<CustomerModel> create(@Body CustomerModel customer);

    // Hethi elli kenet n9sa (Bch nlawjou 3al client b CIN)
    @GET("api/customers/cin/{cin}")
    Call<CustomerModel> getByCin(@Path("cin") String cin);

    // ✅ دالة تبديل كلمة السر (للموبايل)
    @PUT("api/customers/{id}/password")
    Call<CustomerModel> updatePassword(@Path("id") Long id, @Query("newPassword") String newPassword);
}