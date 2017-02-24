package com.evilcorp.firebaseintegration.login;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.evilcorp.firebaseintegration.MyApp;
import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.base.BaseFragment;
import com.evilcorp.firebaseintegration.forgotpassword.ForgotPasswordFragment;
import com.evilcorp.firebaseintegration.helper.Util;
import com.evilcorp.firebaseintegration.register.RegisterFragment;
import com.evilcorp.firebaseintegration.main.MainActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment implements LoginContract.View, View.OnClickListener, TextView.OnEditorActionListener {
    // UI references.
    private TextInputEditText mEmailView;
    private TextInputEditText mPasswordView;

    private TwitterLoginButton twitterLoginButton;

    private LoginContract.Presenter loginPresenter;
    private CallbackManager mFbCallbackManager;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginPresenter = new LoginPresenter(this, new LoginInteractor());
        if(loginPresenter.autoLogin()){
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        mEmailView = (TextInputEditText) root.findViewById(R.id.email);
        mPasswordView = (TextInputEditText) root.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(this);

        Button mEmailSignInButton = (Button) root.findViewById(R.id.email_sign_in_button);
        Button mGuestSignInButton = (Button) root.findViewById(R.id.guest_sign_in_button);
        Button mForgotPassword = (Button) root.findViewById(R.id.forgot_password_button);
        Button mRegisterButton = (Button) root.findViewById(R.id.register_button);
        SignInButton googleButton = (SignInButton) root.findViewById(R.id.google_button);
        setGooglePlusButtonText(googleButton,"Log in with Google");
        LoginButton facebookButton = (LoginButton) root.findViewById(R.id.facebook_button);
        twitterLoginButton = (TwitterLoginButton) root.findViewById(R.id.twitter_button);
        twitterLoginButton.setCallback(loginPresenter.getTwitterHandler());

        mEmailSignInButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        mGuestSignInButton.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);

        //facebookButton.setReadPermissions("email");
        mFbCallbackManager = CallbackManager.Factory.create();
        facebookButton.registerCallback(mFbCallbackManager, loginPresenter.getFacebookHandler());
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFbCallbackManager = null;
        loginPresenter.onDestroy();
    }

    public void googleLogin(){
        showProgress(getString(R.string.login_message));
        Intent signInIntent = MyApp.getGoogleSignInApi().getSignInIntent(MyApp.getGoogleApiClient());
        getActivity().startActivityForResult(signInIntent, MyApp.GOOGLE_AUTH_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showProgress(getString(R.string.login_message));
        Log.d("Data",data.toString());
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MyApp.GOOGLE_AUTH_REQUEST_CODE:
                    loginPresenter.loginWithGoogle(MyApp.getGoogleSignInApi().getSignInResultFromIntent(data));
                    break;
                case MyApp.TWITTER_AUTH_REQUEST_CODE:
                    twitterLoginButton.onActivityResult(requestCode, resultCode, data);
                    break;
                default:
                    mFbCallbackManager.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
        else{
            loginFail(getString(R.string.login_fail));
        }
    }

    public void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Search all the views inside SignInButton for TextView
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            // if the view is instance of TextView then change the text SignInButton
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }


    @Override
    public void loginSuccess(boolean email_verified) {
        dismissProgress();
        Intent mainIntent = new Intent(getActivity(),MainActivity.class);
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
        if(error!=0)
            mEmailView.setError(getString(error));
    }

    @Override
    public void setPasswordError(int error) {
        if(error!=0)
            mPasswordView.setError(getString(error));
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch(viewId){
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
            default:
                break;
        }
    }

    private void guestLogin(){
        if(!Util.isNetworkAvailable(getContext())){
            showAlert(getString(R.string.network_unavailable));
            return;
        }
        showProgress(getString(R.string.login_message));
        loginPresenter.loginAsGuest();
    }

    private void login(){
        if(!Util.isNetworkAvailable(getContext())){
            showAlert(getString(R.string.network_unavailable));
            return;
        }
        showProgress(getString(R.string.login_message));
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean fields_ok = loginPresenter.loginWithEmail(email,password);
        if(!fields_ok){
            dismissProgress();
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            login();
            return true;
        }
        else return false;
    }

}
