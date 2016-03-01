package com.udacity.myappportfolio.network;


import com.udacity.myappportfolio.model.response.DiscoverMovieResponse;
import com.udacity.myappportfolio.model.response.MovieDetailResponse;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Amardeep on 13/2/16.
 */
public class NetworkManager {

    private static ApiService apiService;

    private static Retrofit getRetroFit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        OkHttpClient httpClient = new OkHttpClient.Builder()/*.addInterceptor(logging)*/.build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(Config.UrlConstants.REQUEST_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    private static ApiService getApiService() {
        if (apiService == null) {
            apiService = getRetroFit().create(ApiService.class);
        }
        return apiService;
    }

    public static void requestMovies(String sort, String apiKey, int page, Callback<DiscoverMovieResponse> callback) {
        ApiService service = getApiService();
        Map<String, String> options = new HashMap<>();
        options.put(Config.UrlConstants.SORT_BY, sort);
        options.put(Config.UrlConstants.API_KEY, apiKey);
        options.put(Config.UrlConstants.PAGE, String.valueOf(page));
        Call<DiscoverMovieResponse> model = service.requestMovies(options);
        model.enqueue(callback);
    }

    public static void requestMovieDetails(String apiKey, int movieId, Callback<MovieDetailResponse> callback) {
        ApiService service = getApiService();
        Call<MovieDetailResponse> model = service.requestMovieDetail(movieId, apiKey);
        model.enqueue(callback);
    }
}
