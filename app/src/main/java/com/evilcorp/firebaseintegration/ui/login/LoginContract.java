package com.evilcorp.firebaseintegration.ui.login;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;

/**
 * Created by hristo.stoyanov on 09-Jan-17.
 */

public interface LoginContract {

    interface View {

        void loginSuccess();

        void loginFail(String reason);

        void setEmailError(int error);

        void setPasswordError(int error);

    }

    interface Presenter {

        boolean autoLogin();

        boolean isEmailValid(String email);

        boolean isPasswordValid(String password);

        boolean loginWithEmail(String email, String password);

        void loginAsGuest();

        void loginWithGoogle(GoogleSignInResult result);

        LoginInteractor.TwitterLoginHandler getTwitterHandler();

        LoginInteractor.FacebookLoginHandler getFacebookHandler();

        void onDestroy();

    }
}
