package com.evilcorp.firebaseintegration.main;

import android.net.Uri;

import com.evilcorp.firebaseintegration.model.starwars.Film;

import java.util.List;

public interface MainContract {

    interface View {

        void logoutSuccess();

        void logoutFail();

        void setMessage(String message);

        void setImage(Uri imageUri);

        void showFilms(List<Film> films);

    }

    interface Presenter {


        void logout();

        int getAccountType();

        void getWelcomeMessage();

        void downloadImage();

        void getFilms();

    }
}
