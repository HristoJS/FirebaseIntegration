package com.evilcorp.firebaseintegration.register;


import android.net.Uri;

public interface RegisterContract {
    interface View {

        void registerSuccess();

        void registerFail(String reason);

        void validationError(String error);
    }

    interface Presenter {

        boolean validateFields(String email, String confirm_email, String password, String confirm_password, String name, String country, Uri imageUri);

    }
}
