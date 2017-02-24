package com.evilcorp.firebaseintegration.repository.remote;

import com.evilcorp.firebaseintegration.adapter.ItemTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


class RestApi {
    private static String BASE_URL = "https://swapi.co/api/";
    private static RestInterface apiService;

    public static RestInterface getApi() {

        if(apiService == null){
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                    .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                    .create();

             //This crashes with NoSuchMethodError

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            Retrofit adapter = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();

            apiService = adapter.create(RestInterface.class);
        }
        return apiService;
    }
}
