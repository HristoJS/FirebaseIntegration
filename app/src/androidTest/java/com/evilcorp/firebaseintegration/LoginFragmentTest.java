package com.evilcorp.firebaseintegration;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.evilcorp.firebaseintegration.login.LoginInteractor;
import com.evilcorp.firebaseintegration.login.LoginContract;
import com.evilcorp.firebaseintegration.login.LoginFragment;
import com.evilcorp.firebaseintegration.login.LoginPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;
import static android.support.test.espresso.Espresso.onView;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginFragmentTest {
    private LoginContract.View loginView;

    private LoginFragment loginFragment;

    private LoginInteractor loginInteractor;

    private LoginContract.Presenter loginPresenter;
    @Before
    public void setUp() throws Exception {
        loginFragment = new LoginFragment();
        loginPresenter = new LoginPresenter(loginView, loginInteractor);
    }
    @Test
    public void testPresenterNotNull(){
        assertNotNull(loginPresenter);
    }

    @Test
    public void setGooglePlusButtonText() throws Exception {
        //loginFragment.getView().findViewById(R.id.forgot_password_button);
        onView(withId(R.id.google_button)).perform(click());
//        SignInButton signInButton = (SignInButton) loginFragment.getView().findViewById(R.id.google_button);
//        loginFragment.setGooglePlusButtonText(signInButton,"Test");
//        // Search all the views inside SignInButton for TextView
//        for (int i = 0; i < signInButton.getChildCount(); i++) {
//            View v = signInButton.getChildAt(i);
//
//            // if the view is instance of TextView then change the text SignInButton
//            if (v instanceof TextView) {
//                TextView tv = (TextView) v;
//                assertTrue(tv.getText().equals("Test"));
//                return;
//            }
//        }
    }

    @Test
    public void showProgress() throws Exception {

    }

    @Test
    public void loginSuccess() throws Exception {

    }

    @Test
    public void loginFail() throws Exception {

    }

    @Test
    public void setEmailError() throws Exception {

    }

    @Test
    public void setPasswordError() throws Exception {

    }

    @Test
    public void onClick() throws Exception {

    }

    @Test
    public void onEditorAction() throws Exception {

    }

}