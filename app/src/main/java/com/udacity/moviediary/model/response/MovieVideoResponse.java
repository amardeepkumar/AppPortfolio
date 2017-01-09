package com.udacity.moviediary.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Amardeep on 9/4/16.
 */
public class MovieVideoResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("results")
    private List<VideoResult> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<VideoResult> getResults() {
        return results;
    }

    public void setResults(List<VideoResult> results) {
        this.results = results;
    }
}
