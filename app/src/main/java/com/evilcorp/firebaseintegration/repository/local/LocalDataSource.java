package com.evilcorp.firebaseintegration.repository.local;

import com.evilcorp.firebaseintegration.model.starwars.Film;
import com.evilcorp.firebaseintegration.repository.DataSource;

import java.util.List;

/**
 * Created by hristo.stoyanov on 30-Jan-17.
 */

public class LocalDataSource implements DataSource {

    private static LocalDataSource INSTANCE;

    public static LocalDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocalDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getAllFilms(ResultCallback<List<Film>> callback) {
        callback.failure(new Throwable(""));
    }

    @Override
    public void getFilmById(int id, ResultCallback<Film> callback) {

    }

    @Override
    public void refreshData() {

    }

    @Override
    public void saveFilm(Film film, SimpleCallback callback) {

    }
}
