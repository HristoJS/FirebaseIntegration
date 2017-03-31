package com.evilcorp.firebaseintegration.ui.main;

import android.net.Uri;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.ui.base.BaseContract;

public interface MainContract {

    interface View extends BaseContract.View {

        void logoutSuccess();

        void logoutFail();

        void setMessage(String message);

        void setImage(Uri imageUri);

    }

    interface Presenter extends BaseContract.Presenter {

        void logout();

        boolean isGuest();

        void getWelcomeMessage();

        void downloadImage();

    }

    interface Interactor extends BaseContract.Interactor {

        void downloadImage(final FirebaseCallback<Uri> callback);

        void getWelcomeMessage(final FirebaseCallback<String> callback);

        int getAccountType();

        void deleteUser();

        void logout();

    }
}
