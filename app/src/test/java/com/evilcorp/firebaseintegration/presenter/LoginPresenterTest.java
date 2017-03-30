package com.evilcorp.firebaseintegration.presenter;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.ui.login.LoginContract;
import com.evilcorp.firebaseintegration.ui.login.LoginInteractor;
import com.evilcorp.firebaseintegration.ui.login.LoginPresenter;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class LoginPresenterTest {
    @Mock
    private LoginContract.View loginView;

    @Mock
    private LoginInteractor loginInteractor;

    @Mock
    private GoogleSignInResult result;

    private LoginPresenter mLoginPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mLoginPresenter = new LoginPresenter(loginView, loginInteractor);
    }

    @Test
    public void testNoPassword(){
        mLoginPresenter.loginWithEmail("asd@asd.com","");
        verify(loginView).setEmailError(0);
        verify(loginView).setPasswordError(0);
        verify(loginView).setPasswordError(R.string.error_incorrect_password);
    }

    @Test
    public void testNoEmail(){
        mLoginPresenter.loginWithEmail("","123456");
        verify(loginView).setEmailError(0);
        verify(loginView).setPasswordError(0);
        verify(loginView).setEmailError(R.string.error_invalid_email);
    }

    @Test
    public void testWrongEmail(){
        mLoginPresenter.loginWithEmail("blabla","123456");
        verify(loginView).setEmailError(0);
        verify(loginView).setPasswordError(0);
        verify(loginView).setEmailError(R.string.error_invalid_email);
    }

    @Test
    public void testShortPassword(){
        mLoginPresenter.loginWithEmail("blabla@","1234");
        verify(loginView).setEmailError(0);
        verify(loginView).setPasswordError(0);
        verify(loginView).setPasswordError(R.string.error_incorrect_password);
    }

    @Test
    public void testCorrectCredentials(){
        mLoginPresenter.loginWithEmail("blabla@bla.com","123456");
        verify(loginView).setEmailError(0);
        verify(loginView).setPasswordError(0);
    }

    @Test
    public void testSuccess() {
        mLoginPresenter.success(null);
        verify(loginView).loginSuccess();
    }

    @Test
    public void testFail() {
        mLoginPresenter.fail(new Exception("User not registered."));
        verify(loginView).loginFail("User not registered.");
    }

}