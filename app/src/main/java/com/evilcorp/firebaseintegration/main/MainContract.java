package com.evilcorp.firebaseintegration.main;

import android.net.Uri;

public interface MainContract {

    interface View {

        void logoutSuccess();

        void logoutFail();

        void setMessage(String message);

        void setImage(Uri imageUri);

    }

    interface Presenter {


        void logout();

        int getAccountType();

        void getWelcomeMessage();

        void downloadImage();

    }
}
