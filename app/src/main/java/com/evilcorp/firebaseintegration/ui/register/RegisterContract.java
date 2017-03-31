package com.evilcorp.firebaseintegration.ui.register;


import android.net.Uri;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;
import com.evilcorp.firebaseintegration.ui.base.BaseContract;

public interface RegisterContract {
    interface View extends BaseContract.View {

        void registerSuccess();

    }

    interface Presenter extends BaseContract.Presenter {

        void register(String email, String confirm_email, String password, String confirm_password, String name, String country, Uri imageUri);

    }

    interface Interactor extends BaseContract.Interactor {

        void createUser(UserAccount user, FirebaseCallback<Void> callback);

    }
}
