package com.example.bankapp;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationApi {
    @GET("api/notifications/customer/{id}")
    Call<List<NotificationModel>> getNotifications(@Path("id") Long customerId);

    @POST("api/notifications")
    Call<NotificationModel> create(@Body NotificationModel notification); // ✅ زدنا هذي
}