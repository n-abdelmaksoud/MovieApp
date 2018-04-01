package com.example.android.movieapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Little Princess on 2/24/2018.
 */

public class MovieResponse {

    private List<MovieModel> results;

    public MovieResponse(List<MovieModel> results) {
        this.results = results;
    }

    public List<MovieModel> getResults() {
        return results;
    }

    public void setResults(List<MovieModel> results) {
        this.results = results;
    }



    public static class MovieModel implements Parcelable {


        @SerializedName("poster_path")
        private String posterImage;

        @SerializedName("backdrop_path")
        private String backdropImage;

        @SerializedName("overview")
        private String overview;

        @SerializedName("title")
        private String title;

        @SerializedName("release_date")
        private String date;

        @SerializedName("id")
        private int id;

        @SerializedName("vote_average")
        private double voteAverage;

        public MovieModel(String posterImage, String backdropImage, String overview, String title, String date, int id, double voteAverage) {
            this.posterImage = posterImage;
            this.backdropImage = backdropImage;
            this.overview = overview;
            this.title = title;
            this.date = date;
            this.id = id;
            this.voteAverage = voteAverage;
        }


        protected MovieModel(Parcel in) {
            posterImage = in.readString();
            backdropImage = in.readString();
            overview = in.readString();
            title = in.readString();
            date = in.readString();
            id = in.readInt();
            voteAverage = in.readDouble();
        }


        public String getPosterImage() {
            return posterImage;
        }

        public void setPosterImage(String posterImage) {
            this.posterImage = posterImage;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getVoteAverage() {
            return voteAverage;
        }

        public void setVoteAverage(double voteAverage) {
            this.voteAverage = voteAverage;
        }

        public String getBackdropImage() {
            return backdropImage;
        }

        public void setBackdropImage(String backdropImage) {
            this.backdropImage = backdropImage;
        }

        public String getOverview() {
            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(posterImage);
            dest.writeString(backdropImage);
            dest.writeString(overview);
            dest.writeString(title);
            dest.writeString(date);
            dest.writeInt(id);
            dest.writeDouble(voteAverage);

        }


        public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
            @Override
            public MovieModel createFromParcel(Parcel in) {
                return new MovieModel(in);
            }

            @Override
            public MovieModel[] newArray(int size) {
                return new MovieModel[size];
            }
        };
    }
}
