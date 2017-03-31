package com.evilcorp.firebaseintegration.ui.login;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.ui.base.BaseContract;
import com.evilcorp.firebaseintegration.ui.base.BasePresenter;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;


public class LoginPresenter extends BasePresenter implements LoginContract.Presenter, FirebaseCallback<Void> {
    private static final String TAG = LoginPresenter.class.getSimpleName();

    private final LoginContract.View mLoginView;
    private LoginContract.Interactor mLoginInteractor;

    public LoginPresenter(LoginContract.View view, LoginContract.Interactor interactor) {
        super(view, interactor);
        this.mLoginView = view;
        this.mLoginInteractor = interactor;
        mLoginInteractor.setCallback(this);
    }

    @Override
    public LoginInteractor.TwitterLoginHandler getTwitterHandler() {
        return mLoginInteractor.getTwitterLoginHandler();
    }

    @Override
    public LoginInteractor.FacebookLoginHandler getFacebookHandler() {
        return mLoginInteractor.getFacebookLoginHandler();
    }

    @Override
    public void onDestroy() {
        mLoginInteractor.setCallback(null);
        mLoginInteractor.destroyAllListeners();
        mLoginInteractor = null;
    }

    @Override
    public boolean loginWithEmail(String email, String password) {
        // Reset errors.
        mLoginView.setEmailError(0);
        mLoginView.setPasswordError(0);

        // Check for a valid email address.
        if (!isValidEmail(email)) {
            mLoginView.setEmailError(R.string.error_invalid_email);
            return false;
        }

        // Check for a valid password, if the user entered one.
        if (!isValidPassword(password)) {
            mLoginView.setPasswordError(R.string.error_incorrect_password);
            return false;
        }

        mLoginInteractor.loginWithEmail(email, password);
        return true;
    }

    @Override
    public void loginWithGoogle(GoogleSignInResult result) {
        if (result.isSuccess()) {
            mLoginInteractor.loginWithGoogle(result);
        } else {
            mLoginView.loginFail("Google sign-in failed.");
        }
    }

    @Override
    public void loginAsGuest() {
        mLoginInteractor.loginAsGuest();
        //mAuth.signInAnonymously().addOnCompleteListener(this);
    }

    @Override
    public boolean autoLogin() {
        return mLoginInteractor.isUserLoggedIn();
    }

    @Override
    public void success(Void v) {
        mLoginView.loginSuccess();
    }

    @Override
    public void fail(String error) {
        mLoginView.loginFail(error);
    }
}
