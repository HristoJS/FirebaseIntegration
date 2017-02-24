package com.evilcorp.firebaseintegration.friendlist;

import android.content.Context;

import com.evilcorp.firebaseintegration.model.firebase.UserAccount;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by hristo.stoyanov on 10-Feb-17.
 */

public interface FriendListContract {
    interface View {

        void loadChat(String chatId, String userId);

        void setupRecyclerView(List<UserAccount> users);

        void notifyItemAdded(UserAccount user);

        void notifyDataSetChanged();

    }

    interface Presenter {

        void initChat(String targetUserId);

        void destroy();

    }
}
