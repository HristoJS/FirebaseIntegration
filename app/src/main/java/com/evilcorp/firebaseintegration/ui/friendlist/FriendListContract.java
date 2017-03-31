package com.evilcorp.firebaseintegration.ui.friendlist;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;
import com.evilcorp.firebaseintegration.ui.base.BaseContract;

import java.util.List;

/**
 * Created by hristo.stoyanov on 10-Feb-17.
 */

public interface FriendListContract {
    interface View extends BaseContract.View {

        void loadChat(String chatId, String userId);

        void setupRecyclerView(List<UserAccount> users);

        void notifyItemAdded(UserAccount user);

        void notifyDataSetChanged();

    }

    interface Presenter extends BaseContract.Presenter {

        void initChat(String targetUserId);

    }

    interface Interactor extends BaseContract.Interactor {

        void initChat(String targetUserId, FirebaseCallback<String> callback);

        List<UserAccount> getUserList(FirebaseCallback<Void> callback);
    }
}
