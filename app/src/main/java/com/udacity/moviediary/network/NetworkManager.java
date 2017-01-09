package com.udacity.moviediary.network;


import com.udacity.moviediary.BuildConfig;
import com.udacity.moviediary.model.response.DiscoverMovieResponse;
import com.udacity.moviediary.model.response.MovieDetailResponse;
import com.udacity.moviediary.model.response.MovieReviewResponse;
import com.udacity.moviediary.model.response.MovieVideoResponse;

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
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        return  new Retrofit.Builder()
                .baseUrl(Config.UrlConstants.REQUEST_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static ApiService getApiService() {
        if (apiService == null) {
            apiService = getRetroFit().create(ApiService.class);
        }
        return apiService;
    }

    public static void requestMovies(String sort, int page, Callback<DiscoverMovieResponse> callback) {
        ApiService service = getApiService();
        Map<String, String> options = new HashMap<>();
        options.put(Config.UrlConstants.PAGE, String.valueOf(page));
        options.put(Config.UrlConstants.SORT_BY, sort);
        options.put(Config.UrlConstants.API_KEY, BuildConfig.OPEN_WEATHER_MAP_API_KEY);
        Call<DiscoverMovieResponse> model = service.requestMovies(options);
        model.enqueue(callback);
    }

    public static void requestMovieDetails(String movieId, Callback<MovieDetailResponse> callback) {
        ApiService service = getApiService();
        Call<MovieDetailResponse> model = service.requestMovieDetail(movieId, BuildConfig.OPEN_WEATHER_MAP_API_KEY);
        model.enqueue(callback);
    }

    public static void requestMovieTrailers(String movieId, Callback<MovieVideoResponse> callback) {
        ApiService service = getApiService();
        Call<MovieVideoResponse> model = service.requestMovieTrailers(movieId, BuildConfig.OPEN_WEATHER_MAP_API_KEY);
        model.enqueue(callback);
    }

    public static void requestMovieReviews(String movieId, Callback<MovieReviewResponse> callback) {
        ApiService service = getApiService();
        Call<MovieReviewResponse> model = service.requestMovieReviews(movieId, BuildConfig.OPEN_WEATHER_MAP_API_KEY);
        model.enqueue(callback);
    }
}
