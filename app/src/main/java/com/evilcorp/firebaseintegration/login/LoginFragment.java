package com.evilcorp.firebaseintegration.login;


import com.google.android.gms.common.SignInButton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.evilcorp.firebaseintegration.MyApp;
import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.base.BaseFragment;
import com.evilcorp.firebaseintegration.forgotpassword.ForgotPasswordFragment;
import com.evilcorp.firebaseintegration.helper.NetworkHelper;
import com.evilcorp.firebaseintegration.main.MainActivity;
import com.evilcorp.firebaseintegration.register.RegisterFragment;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Arrays;
import java.util.Collection;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment implements LoginContract.View, View.OnClickListener, TextView.OnEditorActionListener {
    // UI references.
    private TextInputEditText mEmailView;
    private TextInputEditText mPasswordView;

    private LoginContract.Presenter mLoginPresenter;
    private CallbackManager mFbCallbackManager;
    private LoginManager mFacebookLoginManger;
    private TwitterAuthClient mTwitterAuthClient;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginPresenter = new LoginPresenter(this, new LoginInteractor());
        if (mLoginPresenter.autoLogin()) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        mEmailView = (TextInputEditText) root.findViewById(R.id.email_edittext);
        mPasswordView = (TextInputEditText) root.findViewById(R.id.password_edittext);
        mPasswordView.setOnEditorActionListener(this);

        AppCompatButton mEmailSignInButton = (AppCompatButton) root.findViewById(R.id.email_sign_in_button);
        AppCompatButton mGuestSignInButton = (AppCompatButton) root.findViewById(R.id.guest_sign_in_button);
        AppCompatButton mForgotPassword = (AppCompatButton) root.findViewById(R.id.forgot_password_button);
        AppCompatButton mRegisterButton = (AppCompatButton) root.findViewById(R.id.register_button);
        AppCompatButton googleButton = (AppCompatButton) root.findViewById(R.id.google_button);
        AppCompatButton facebookButton = (AppCompatButton) root.findViewById(R.id.facebook_button);
        AppCompatButton twitterLoginButton = (AppCompatButton) root.findViewById(R.id.twitter_button);

        mTwitterAuthClient = new TwitterAuthClient();

        mEmailSignInButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        mGuestSignInButton.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
        twitterLoginButton.setOnClickListener(this);

        //facebookButton.setReadPermissions("email");
        mFbCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginManger = LoginManager.getInstance();
        mFacebookLoginManger.registerCallback(mFbCallbackManager, mLoginPresenter.getFacebookHandler());
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFbCallbackManager = null;
        mLoginPresenter.onDestroy();
    }

    public void googleLogin() {
        showProgress(getString(R.string.login_message));
        Intent signInIntent = MyApp.getGoogleSignInApi().getSignInIntent(MyApp.getGoogleApiClient());
        getActivity().startActivityForResult(signInIntent, MyApp.GOOGLE_AUTH_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showProgress(getString(R.string.login_message));
        Log.d("Data", data.toString());
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MyApp.GOOGLE_AUTH_REQUEST_CODE:
                    mLoginPresenter.loginWithGoogle(MyApp.getGoogleSignInApi().getSignInResultFromIntent(data));
                    break;
                case MyApp.TWITTER_AUTH_REQUEST_CODE:
                    mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
                    break;
                default:
                    mFbCallbackManager.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        } else {
            loginFail(getString(R.string.login_fail));
        }
    }

    @Override
    public void loginSuccess(boolean email_verified) {
        dismissProgress();
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
        getActivity().finish();
    }

    @Override
    public void loginFail(String reason) {
        dismissProgress();
        showAlert(reason);
    }

    @Override
    public void setEmailError(int error) {
        if (error != 0)
            mEmailView.setError(getString(error));
    }

    @Override
    public void setPasswordError(int error) {
        if (error != 0)
            mPasswordView.setError(getString(error));
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.email_sign_in_button:
                login();
                break;
            case R.id.google_button:
                googleLogin();
                break;
            case R.id.register_button:
                ((LoginActivity) getActivity()).loadFragment(new RegisterFragment(), true);
                break;
            case R.id.guest_sign_in_button:
                guestLogin();
                break;
            case R.id.forgot_password_button:
                ((LoginActivity) getActivity()).loadFragment(new ForgotPasswordFragment(), true);
                break;
            case R.id.twitter_button:
                mTwitterAuthClient.authorize(getActivity(), mLoginPresenter.getTwitterHandler());
                break;
            case R.id.facebook_button:
                Collection<String> permissions = Arrays.asList("public_profile", "user_friends");
                mFacebookLoginManger.logInWithReadPermissions(getActivity(), permissions);
                break;
            default:
                break;
        }
    }

    private void guestLogin() {
        if (!NetworkHelper.isNetworkAvailable(getContext())) {
            showAlert(getString(R.string.network_unavailable));
            return;
        }
        showProgress(getString(R.string.login_message));
        mLoginPresenter.loginAsGuest();
    }

    private void login() {
        if (!NetworkHelper.isNetworkAvailable(getContext())) {
            showAlert(getString(R.string.network_unavailable));
            return;
        }
        showProgress(getString(R.string.login_message));
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean fields_ok = mLoginPresenter.loginWithEmail(email, password);
        if (!fields_ok) {
            dismissProgress();
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            login();
            return true;
        } else return false;
    }

}
