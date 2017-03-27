package com.evilcorp.firebaseintegration.friendlist;

import android.util.Log;

import com.evilcorp.firebaseintegration.MyApp;
import com.evilcorp.firebaseintegration.model.firebase.Chat;
import com.evilcorp.firebaseintegration.model.firebase.UserAccount;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


class FriendListPresenter implements FriendListContract.Presenter {
    private static final String TAG = FriendListPresenter.class.getSimpleName();
    private static final String CHATS = "chats";
    private static final String USERS = "mUsers";

    private FriendListContract.View mChatView;
    private List<UserAccount> mUsers;
    private boolean mChatFound = false;
    private UserListener mUserListener;
    private ChatListener mChatListener;

    FriendListPresenter(FriendListContract.View view) {
        this.mChatView = view;
        getUserList();
    }

    private DatabaseReference getDBRef(String tableId) {
        return FirebaseDatabase.getInstance().getReference().child(tableId);
    }

    @Override
    public void destroy() {
        if (mUserListener != null) {
            getDBRef(USERS).removeEventListener(mUserListener);
            mUserListener = null;
        }
        if (mChatListener != null) {
            getDBRef(CHATS).removeEventListener(mChatListener);
            mChatListener = null;
        }
    }

    private void getUserList() {
        mUsers = new ArrayList<>();
        mUserListener = new UserListener();
        mChatView.setupRecyclerView(mUsers);
        getDBRef(USERS).orderByChild("lastOnline").addChildEventListener(mUserListener);
    }

    private void createChat(String targetUserId) {
        DatabaseReference new_chat = getDBRef(CHATS).push();
        List<String> ids = new ArrayList<>();
        ids.add(targetUserId);
        ids.add(MyApp.getCurrentAccount().getId());
        Chat chat = new Chat(new_chat.getKey(), "", "New Chat", ids);
        new_chat.setValue(chat);
        mChatView.loadChat(new_chat.getKey(), targetUserId);
    }

    @Override
    public void initChat(String targetUserId) {
        mChatListener = new ChatListener(targetUserId);
        getDBRef(CHATS).addValueEventListener(mChatListener);
    }

    private final class ChatListener implements ValueEventListener {
        private String targetUserId;

        ChatListener(String targetUserId) {
            this.targetUserId = targetUserId;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot chat : dataSnapshot.getChildren()) {
                List<String> ids = chat.getValue(Chat.class).getUserIds();
                if (ids.contains(targetUserId) && ids.contains(MyApp.getCurrentAccount().getId())) {
                    mChatFound = true;
                    mChatView.loadChat(chat.getKey(), targetUserId);
                }
            }
            if (!mChatFound) {
                createChat(targetUserId);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, databaseError.getMessage());
        }
    }

    private final class UserListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Log.d(TAG, "User Added.");
            UserAccount user = dataSnapshot.getValue(UserAccount.class);
            if (!user.getId().equals(MyApp.getCurrentAccount().getId()) && !mUsers.contains(user)) {
                mUsers.add(user);
                mChatView.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Log.d(TAG, "User Changed.");
            UserAccount updated_user = dataSnapshot.getValue(UserAccount.class);
            for (UserAccount user : mUsers) {
                if (user.getId().equals(updated_user.getId())) {
                    Collections.replaceAll(mUsers, user, updated_user);
                    mChatView.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG, "User Removed.");
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            Log.d(TAG, "User Moved.");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, databaseError.getMessage());
        }
    }
}
