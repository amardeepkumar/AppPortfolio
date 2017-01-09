package com.udacity.moviediary.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Amardeep on 21/2/16.
 */
public class DiscoverMovieResponse {
    @SerializedName("page")
    private int mPage;
    @SerializedName("results")
    private List<MovieResult> mResults;
    @SerializedName("total_results")
    private int mTotalResults;
    @SerializedName("total_pages")
    private int mTotalPages;

    public int getPage() {
        return mPage;
    }

    public void setPage(int mPage) {
        this.mPage = mPage;
    }

    public List<MovieResult> getResults() {
        return mResults;
    }

    public void setResults(List<MovieResult> mResults) {
        this.mResults = mResults;
    }

    public int getTotalResults() {
        return mTotalResults;
    }

    public void setTotalResults(int mTotalResults) {
        this.mTotalResults = mTotalResults;
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(int mTotalPages) {
        this.mTotalPages = mTotalPages;
    }
}
