package com.example.android.movieapp.models;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Little Princess on 2/25/2018.
 */

public class ModifyMovieList {



    private ModifyMovieList(){

    }

    /*{@link modifyList} is used to delete some movies from my list.
  * I can't display the images of these movies because they are against
  * Islamic Laws and i'm trying to stick with my religion laws. */


    public static void modifyList(List<MovieResponse.MovieModel> list, int[] deletedMoviesID){
        Iterator<MovieResponse.MovieModel> iterator= list.iterator();
        while (iterator.hasNext()) {
            int id = iterator.next().getId();
            for (int deletedMovie : deletedMoviesID) {
                if (id == deletedMovie) {
                   iterator.remove();
                    break;
                }
            }
        }

    }
}
