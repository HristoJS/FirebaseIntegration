package com.evilcorp.firebaseintegration.repository;

import com.evilcorp.firebaseintegration.model.starwars.Film;

import java.util.List;


public interface DataSource {
    interface ResultCallback<T>{
        void success(T response);
        void failure(Throwable throwable);
    }

    interface SimpleCallback{
        void success();
        void failure(Throwable throwable);
    }

    void getAllFilms(ResultCallback<List<Film>> callback);

    void getFilmById(int id, ResultCallback<Film> callback);

    void refreshData();

    void saveFilm(Film film, SimpleCallback callback);
}
