package com.evilcorp.firebaseintegration.ui.friendlist;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;
import com.evilcorp.firebaseintegration.ui.base.BasePresenter;

import java.util.List;

class FriendListPresenter extends BasePresenter implements FriendListContract.Presenter {
    private static final String TAG = FriendListPresenter.class.getSimpleName();

    private final FriendListContract.View mView;
    private final FriendListContract.Interactor mInteractor;

    FriendListPresenter(FriendListContract.View view, FriendListContract.Interactor interactor) {
        super(view, interactor);
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
            public void fail(String error) {
                if (mView.isActive()) {
                    mView.onError(error);
                }
            }
        });
        mView.setupRecyclerView(users);
    }

    @Override
    public void initChat(final String targetUserId) {
        mInteractor.initChat(targetUserId, new FirebaseCallback<String>() {
            @Override
            public void success(String result) {
                mView.loadChat(result, targetUserId);
            }

            @Override
            public void fail(String error) {
                if (mView.isActive()) {
                    mView.onError(error);
                }
            }
        });
    }
}
