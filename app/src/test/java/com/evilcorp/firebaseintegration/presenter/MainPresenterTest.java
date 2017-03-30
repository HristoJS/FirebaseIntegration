package com.evilcorp.firebaseintegration.presenter;

import android.net.Uri;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.ui.main.MainContract;
import com.evilcorp.firebaseintegration.ui.main.MainInteractor;
import com.evilcorp.firebaseintegration.ui.main.MainPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Created by hristo.stoyanov on 24-Feb-17.
 */
public class MainPresenterTest {
    private MainPresenter mMainPresenter;

    @Mock
    MainContract.View mainView;

    @Mock
    MainInteractor mainInteractor;

    @Mock
    FirebaseCallback<Uri> downloadImageCallback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mMainPresenter = new MainPresenter(mainView, mainInteractor);
    }

    @Test
    public void testDownloadImage() throws Exception {
        mMainPresenter.downloadImage();
        wait(5000);
        verify(mainView).setImage(any(Uri.class));
    }

    @Test
    public void logout() throws Exception {

    }

    @Test
    public void getWelcomeMessage() throws Exception {

    }

    @Test
    public void getAccountType() throws Exception {

    }

    @Test
    public void getFilms() throws Exception {

    }

}