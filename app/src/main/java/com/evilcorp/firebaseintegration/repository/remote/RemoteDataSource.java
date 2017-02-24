package com.evilcorp.firebaseintegration.repository.remote;

import com.evilcorp.firebaseintegration.model.starwars.Film;
import com.evilcorp.firebaseintegration.repository.DataSource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hristo.stoyanov on 30-Jan-17.
 */

public class RemoteDataSource implements DataSource {
    private RestInterface restApi;
    private static RemoteDataSource INSTANCE;

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private RemoteDataSource() {
        this.restApi = RestApi.getApi();
    }

    @Override
    public void getAllFilms(final ResultCallback<List<Film>> callback) {
        restApi.getAllFilms().enqueue(new Callback<List<Film>>() {
            @Override
            public void onResponse(Call<List<Film>> call, Response<List<Film>> response) {
                if(response.isSuccessful())
                callback.success(response.body());
                else {
                    String error = response.errorBody().toString();
                }
            }

            @Override
            public void onFailure(Call<List<Film>> call, Throwable t) {
                t.printStackTrace();
                callback.failure(t);
            }
        });
    }

    @Override
    public void getFilmById(int id,final ResultCallback<Film> callback) {
        restApi.getFilmById(id).enqueue(new Callback<Film>() {
            @Override
            public void onResponse(Call<Film> call, Response<Film> response) {
                callback.success(response.body());
            }

            @Override
            public void onFailure(Call<Film> call, Throwable t) {
                callback.failure(t);
            }
        });
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void saveFilm(Film film, SimpleCallback callback) {

    }
}
