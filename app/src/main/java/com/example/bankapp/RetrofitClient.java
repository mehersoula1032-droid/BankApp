package com.example.bankapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String URL_CUSTOMER = "http://192.168.137.1:8082/";
    private static final String URL_ACCOUNT = "http://192.168.137.1:8083/";
    private static final String URL_TRANSACTION = "http://192.168.137.1:8085/";
    private static final String URL_NOTIFICATION = "http://192.168.137.1:8086/";
    private static final String URL_CARD = "http://192.168.137.1:8084/";

    public static CustomerApi getCustomerApi() {
        return new Retrofit.Builder().baseUrl(URL_CUSTOMER).addConverterFactory(GsonConverterFactory.create()).build().create(CustomerApi.class);
    }

    public static AccountApi getAccountApi() {
        return new Retrofit.Builder().baseUrl(URL_ACCOUNT).addConverterFactory(GsonConverterFactory.create()).build().create(AccountApi.class);
    }

    public static TransactionApi getTransactionApi() {
        return new Retrofit.Builder().baseUrl(URL_TRANSACTION).addConverterFactory(GsonConverterFactory.create()).build().create(TransactionApi.class);
    }

    public static NotificationApi getNotificationApi() {
        return new Retrofit.Builder().baseUrl(URL_NOTIFICATION).addConverterFactory(GsonConverterFactory.create()).build().create(NotificationApi.class);
    }

    public static CardApi getCardApi() {
        return new Retrofit.Builder().baseUrl(URL_CARD).addConverterFactory(GsonConverterFactory.create()).build().create(CardApi.class);
    }
}