package com.evilcorp.firebaseintegration.repository.remote;



import com.evilcorp.firebaseintegration.model.starwars.Film;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RestInterface {
    @GET("films/")
    Call<List<Film>> getAllFilms();

    @GET("films/{id}/")
    Call<Film> getFilmById(@Path("id") int id);
}
