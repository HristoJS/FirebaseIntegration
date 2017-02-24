package com.evilcorp.firebaseintegration.repository;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evilcorp.firebaseintegration.model.starwars.Film;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hristo.stoyanov on 30-Jan-17.
 */

public class DataRepository implements DataSource {
    private static DataRepository INSTANCE = null;
    private static final String TAG = DataRepository.class.getSimpleName();

    private DataSource mLocalDataSource;
    private DataSource mRemoteDataSource;
    private Map<Integer,Film> mCachedFilms;
    private boolean mCacheIsDirty = false;

    private DataRepository(@NonNull DataSource localDataSource,
                          @NonNull DataSource remoteDataSource){
        this.mLocalDataSource = localDataSource;
        this.mRemoteDataSource = remoteDataSource;
    }

    public static DataRepository getInstance(DataSource localDataSource,
                                             DataSource remoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new DataRepository(localDataSource, remoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getAllFilms(final ResultCallback<List<Film>> callback) {
        if(mCachedFilms != null&& !mCacheIsDirty){
            callback.success(new ArrayList<>(mCachedFilms.values()));
            return;
        }
        mLocalDataSource.getAllFilms(new ResultCallback<List<Film>>() {
            @Override
            public void success(List<Film> response) {
                refreshCache(response);
                callback.success(response);
            }

            @Override
            public void failure(Throwable throwable) {
                mRemoteDataSource.getAllFilms(new ResultCallback<List<Film>>() {
                    @Override
                    public void success(List<Film> response) {
                        callback.success(response);
                        for (Film film: response) {
                            mLocalDataSource.saveFilm(film, new SimpleCallback() {
                                @Override
                                public void success() {
                                    Log.d(TAG,"Saved entry to DB.");
                                }

                                @Override
                                public void failure(Throwable throwable) {
                                    Log.d(TAG,"Error");
                                }
                            });
                        }
                    }

                    @Override
                    public void failure(Throwable throwable) {
                        callback.failure(throwable);
                    }
                });
            }
        });
    }

    private void refreshCache(List<Film> films) {
        if (mCachedFilms == null) {
            mCachedFilms = new LinkedHashMap<>();
        }
        mCachedFilms.clear();
        for (Film film : films) {
            mCachedFilms.put(film.getEpisodeId(), film);
        }
        mCacheIsDirty = false;
    }

    @Override
    public void getFilmById(int id, ResultCallback<Film> callback) {
    }

    @Override
    public void refreshData() {
        mCacheIsDirty = true;
    }

    @Override
    public void saveFilm(Film film, SimpleCallback callback) {
        mLocalDataSource.saveFilm(film, callback);
        mRemoteDataSource.saveFilm(film, callback);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedFilms == null) {
            mCachedFilms = new LinkedHashMap<>();
        }
        mCachedFilms.put(film.getEpisodeId(), film);
    }
}
