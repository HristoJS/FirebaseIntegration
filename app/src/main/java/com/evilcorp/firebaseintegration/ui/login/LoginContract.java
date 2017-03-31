package com.evilcorp.firebaseintegration.ui.login;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.ui.base.BaseContract;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

/**
 * Created by hristo.stoyanov on 09-Jan-17.
 */

public interface LoginContract {

    interface View extends BaseContract.View {

        void loginSuccess();

        void loginFail(String reason);

        void setEmailError(int error);

        void setPasswordError(int error);

    }

    interface Presenter extends BaseContract.Presenter {

        boolean autoLogin();

        boolean loginWithEmail(String email, String password);

        void loginAsGuest();

        void loginWithGoogle(GoogleSignInResult result);

        LoginInteractor.TwitterLoginHandler getTwitterHandler();

        LoginInteractor.FacebookLoginHandler getFacebookHandler();

    }

    interface Interactor extends BaseContract.Interactor {

        void setCallback(FirebaseCallback<Void> callback);

        void loginWithEmail(String email, String password);

        void loginAsGuest();

        void loginWithGoogle(GoogleSignInResult result);

        boolean isUserLoggedIn();

        com.evilcorp.firebaseintegration.ui.login.LoginInteractor.TwitterLoginHandler getTwitterLoginHandler();

        com.evilcorp.firebaseintegration.ui.login.LoginInteractor.FacebookLoginHandler getFacebookLoginHandler();

    }
}
