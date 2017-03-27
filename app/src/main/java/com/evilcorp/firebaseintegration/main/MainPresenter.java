package com.evilcorp.firebaseintegration.main;

import android.net.Uri;

import com.evilcorp.firebaseintegration.MyApp;
import com.evilcorp.firebaseintegration.helper.FirebaseCallback;
import com.evilcorp.firebaseintegration.model.firebase.AccountType;


public class MainPresenter implements MainContract.Presenter {
    private static final String TAG = MainPresenter.class.getSimpleName();
    private MainContract.View mainView;
    private MainInteractor mMainInteractor;


    public MainPresenter(MainContract.View view, MainInteractor interactor) {
        this.mainView = view;
        this.mMainInteractor = interactor;
    }

    @Override
    public void downloadImage(){
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
        }
        catch (Exception ex){
            ex.printStackTrace();
            mainView.logoutFail();
        }
        finally {
            if(getAccountType() == AccountType.GUEST) {
                mMainInteractor.deleteUser();
            }
            else {
                MyApp.logout();
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
    public int getAccountType() {
        return mMainInteractor.getAccountType();
    }


}
