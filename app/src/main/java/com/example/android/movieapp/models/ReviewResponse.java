package com.example.android.movieapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Little Princess on 3/26/2018.
 */

public class ReviewResponse {

    @SerializedName("results")
    private List<Review> results;

    public ReviewResponse(List<Review> results) {
        this.results = results;
    }

    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }

    /* static class holding Video Info*/

    public static class Review {
        @SerializedName("content")
        private String content;

        @SerializedName("author")
        private String author;

        public Review(String content, String author) {
            this.content = content;
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }
}
