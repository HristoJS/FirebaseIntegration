package com.evilcorp.firebaseintegration.ui.main;

import android.net.Uri;
import android.util.Log;

import com.evilcorp.firebaseintegration.ChatterinoApp;
import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.data.firebase.model.AccountType;
import com.evilcorp.firebaseintegration.ui.base.BasePresenter;


public class MainPresenter extends BasePresenter implements MainContract.Presenter {
    private static final String TAG = MainPresenter.class.getSimpleName();
    private final MainContract.View mainView;
    private final MainContract.Interactor mMainInteractor;

    public MainPresenter(MainContract.View view, MainContract.Interactor interactor) {
        super(view, interactor);
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
            public void fail(String error) {
                if (mainView.isActive()) {
                    mainView.onError(error);
                }
            }
        });
    }

    @Override
    public void logout() {
        try {
            mMainInteractor.logout();
        } catch (Exception ex) {
            Log.e(TAG, "Trying to logout", ex);
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
            public void fail(String error) {
                if (mainView.isActive()) {
                    mainView.onError(error);
                }
            }
        });
    }

    @Override
    public boolean isGuest() {
        return mMainInteractor.getAccountType() == AccountType.GUEST;
    }

}
