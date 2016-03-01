package com.udacity.myappportfolio.network;

import com.udacity.myappportfolio.model.response.DiscoverMovieResponse;
import com.udacity.myappportfolio.model.response.MovieDetailResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Amardeep on 7/2/16.
 */
public interface ApiService {

    @GET("/3/discover/movie")
    Call<DiscoverMovieResponse> requestMovies(@QueryMap Map<String, String> options);

    @GET("/3/movie/{id}")
    Call<MovieDetailResponse> requestMovieDetail(@Path(Config.UrlConstants.ID) int movieId,
                                                 @Query(Config.UrlConstants.API_KEY) String apiKey);
}
