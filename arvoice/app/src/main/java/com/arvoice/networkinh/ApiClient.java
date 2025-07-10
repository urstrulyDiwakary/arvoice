package com.arvoice.networkinh;
// this is the new updated code for this game

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;

    public static Retrofit getPostService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClientd = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .followRedirects(true)
                .followSslRedirects(true)
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS);
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(WebApi.BASEURL)
                    .addConverterFactory(ScalarsConverterFactory.create()) // Important for Call<String>
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClientd.build())
                    .build();
        }
        return retrofit;
    }

}
