package com.evilcorp.firebaseintegration.login;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.AuthCredential;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by hristo.stoyanov on 09-Jan-17.
 */

public interface LoginContract {

    interface View {

        void loginSuccess(boolean email_verified);

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
