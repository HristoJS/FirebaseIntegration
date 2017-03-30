package com.evilcorp.firebaseintegration.ui.main;

import android.net.Uri;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;

public interface MainContract {

    interface View {

        void logoutSuccess();

        void logoutFail();

        void setMessage(String message);

        void setImage(Uri imageUri);

    }

    interface Presenter {

        void logout();

        boolean isGuest();

        void getWelcomeMessage();

        void downloadImage();

    }

    interface Interactor {

        void downloadImage(final FirebaseCallback<Uri> callback);

        void getWelcomeMessage(final FirebaseCallback<String> callback);

        int getAccountType();

        void deleteUser();

        void logout();

    }
}
