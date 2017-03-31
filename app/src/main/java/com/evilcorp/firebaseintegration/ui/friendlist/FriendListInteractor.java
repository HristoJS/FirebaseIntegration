package com.evilcorp.firebaseintegration.ui.friendlist;

import android.util.Log;

import com.evilcorp.firebaseintegration.ChatterinoApp;
import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.data.firebase.FirebaseInteractor;
import com.evilcorp.firebaseintegration.data.firebase.model.AccountType;
import com.evilcorp.firebaseintegration.data.firebase.model.Chat;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;
import com.evilcorp.firebaseintegration.helper.Util;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hristo.stoyanov on 3/30/2017.
 */

class FriendListInteractor extends FirebaseInteractor implements FriendListContract.Interactor {
    private static final String TAG = FriendListInteractor.class.getSimpleName();

    private List<UserAccount> mUsers;

    private ChildEventListener mUserListener;
    private ValueEventListener mChatListener;

    @Override
    public void destroyAllListeners() {
        destroyListener(mChatListener);
        destroyListener(mUserListener);
    }

    @Override
    public List<UserAccount> getUserList(final FirebaseCallback<Void> callback) {
        mUsers = new ArrayList<>();
        mUserListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "User Added.");
                UserAccount user = dataSnapshot.getValue(UserAccount.class);
                UserAccount currentUser = ChatterinoApp.getCurrentAccount();
                if (currentUser == null || currentUser.getAccountType() == AccountType.GUEST) {
                    return;
                }
                if (!Util.equals(user.getId(), currentUser.getId()) && !mUsers.contains(user)) {
                    mUsers.add(user);
                    callback.success(null);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "User Changed.");
                UserAccount updated_user = dataSnapshot.getValue(UserAccount.class);
                if (updated_user.getAccountType() == AccountType.GUEST) {
                    return;
                }
                if (updateUser(updated_user)) {
                    callback.success(null);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "User Removed.");
                UserAccount deleted_user = dataSnapshot.getValue(UserAccount.class);
                mUsers.remove(deleted_user);
                callback.success(null);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "User Moved.");
                UserAccount moved_user = dataSnapshot.getValue(UserAccount.class);
                if (mUsers.contains(moved_user)) {
                    if (updateUser(moved_user)) {
                        callback.success(null);
                    } else throw new NullPointerException("User not found");
                } else {
                    mUsers.add(moved_user);
                    callback.success(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
                callback.fail("Unable to get user list");
            }
        };
        mUsersTable.addChildEventListener(mUserListener);
        return mUsers;
    }

    private boolean updateUser(UserAccount updated_user) {
        for (UserAccount user : mUsers) {
            if (Util.equals(user.getId(), updated_user.getId())) {
                Collections.replaceAll(mUsers, user, updated_user);
                return true;
            }
        }
        return false;
    }

    @Override
    public void initChat(final String targetUserId, final FirebaseCallback<String> callback) {
        //mChatListener = new ChatListener(targetUserId);
        //mChatsTable.addValueEventListener(mChatListener);
        final String currentAccountId = ChatterinoApp.getCurrentAccount().getId();

        mChatListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot chat : dataSnapshot.getChildren()) {
                    List<String> ids = chat.getValue(Chat.class).getUserIds();
                    if (ids.contains(targetUserId) && ids.contains(currentAccountId)) {
                        callback.success(chat.getKey());
                        return;
                    }
                }
                DatabaseReference new_chat = mChatsTable.push();
                List<String> ids = new ArrayList<>();
                ids.add(targetUserId);
                ids.add(currentAccountId);
                Chat chat = new Chat(new_chat.getKey(), "", "New Chat" + "", ids);
                new_chat.setValue(chat);
                callback.success(new_chat.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
                callback.fail("Unable to start chat");
            }
        };
        mChatsTable.addValueEventListener(mChatListener);
    }
}
