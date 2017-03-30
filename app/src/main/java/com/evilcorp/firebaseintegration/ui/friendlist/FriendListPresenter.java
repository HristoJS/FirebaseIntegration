package com.evilcorp.firebaseintegration.ui.friendlist;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.data.firebase.model.UserAccount;

import java.util.List;

class FriendListPresenter implements FriendListContract.Presenter {
    private static final String TAG = FriendListPresenter.class.getSimpleName();

    private FriendListContract.View mView;
    private FriendListContract.Interactor mInteractor;

    FriendListPresenter(FriendListContract.View view, FriendListContract.Interactor interactor) {
        mView = view;
        mInteractor = interactor;
    }

    @Override
    public void onCreate() {
        List<UserAccount> users = mInteractor.getUserList(new FirebaseCallback<Void>() {
            @Override
            public void success(Void result) {
                mView.notifyDataSetChanged();
            }

            @Override
            public void fail(Exception exception) {
                mView.onError(exception.getLocalizedMessage());
            }
        });
        mView.setupRecyclerView(users);
    }

    @Override
    public void onDestroy() {
        mInteractor.destroyListeners();
    }

    @Override
    public void initChat(final String targetUserId) {
        mInteractor.initChat(targetUserId, new FirebaseCallback<String>() {
            @Override
            public void success(String result) {
                mView.loadChat(result, targetUserId);
            }

            @Override
            public void fail(Exception exception) {
                mView.onError(exception.getLocalizedMessage());
            }
        });
    }
}
