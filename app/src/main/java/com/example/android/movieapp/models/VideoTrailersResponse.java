package com.example.android.movieapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Little Princess on 3/26/2018.
 */

public class VideoTrailersResponse {
    @SerializedName("results")
    private List<Video> results;

    public VideoTrailersResponse(List<Video> results) {
        this.results = results;
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }

    /* static class holding Video Info*/

    public static class Video {
        @SerializedName("key")
        private String key;

        public Video(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
