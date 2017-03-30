package com.evilcorp.firebaseintegration.ui.main;

import android.net.Uri;

import com.evilcorp.firebaseintegration.ChatterinoApp;
import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.data.firebase.model.AccountType;


public class MainPresenter implements MainContract.Presenter {
    private static final String TAG = MainPresenter.class.getSimpleName();
    private MainContract.View mainView;
    private MainContract.Interactor mMainInteractor;

    public MainPresenter(MainContract.View view, MainContract.Interactor interactor) {
        this.mainView = view;
        this.mMainInteractor = interactor;
    }

    @Override
    public void downloadImage() {
        mMainInteractor.downloadImage(new FirebaseCallback<Uri>() {
            @Override
            public void success(Uri result) {
                mainView.setImage(result);
            }

            @Override
            public void fail(Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void logout() {
        try {
            mMainInteractor.logout();
        } catch (Exception ex) {
            ex.printStackTrace();
            mainView.logoutFail();
        } finally {
            if (isGuest()) {
                mMainInteractor.deleteUser();
            } else {
                ChatterinoApp.logout();
            }
            mainView.logoutSuccess();
        }
    }

    @Override
    public void getWelcomeMessage() {
        mMainInteractor.getWelcomeMessage(new FirebaseCallback<String>() {
            @Override
            public void success(String result) {
                mainView.setMessage(result);
            }

            @Override
            public void fail(Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public boolean isGuest() {
        return mMainInteractor.getAccountType() == AccountType.GUEST;
    }

}
