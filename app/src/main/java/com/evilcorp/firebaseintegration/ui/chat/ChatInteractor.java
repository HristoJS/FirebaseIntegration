package com.evilcorp.firebaseintegration.ui.chat;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseInteractor;
import com.evilcorp.firebaseintegration.data.firebase.model.Chat;
import com.evilcorp.firebaseintegration.data.firebase.model.Message;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;
import com.evilcorp.firebaseintegration.helper.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hristo.stoyanov on 15-Feb-17.
 */

class ChatInteractor extends FirebaseInteractor implements ChatContract.Interactor {
    private static final String TAG = ChatInteractor.class.getSimpleName();
    private static final String TITLE = "title";
    private final DatabaseReference mCurrentChat;
    private final DatabaseReference mCurrentChatMessages;
    private final List<UserAccount> mChatParticipants;
    private ChatListener mChatListener;
    private UserListener mUserListener;
    private ChatStatusListener mChatStatusListener;
    private final String mTargetUserId;

    interface ChatStatusListener {

        void initComplete(List<UserAccount> chatParticipants, String targetUserName);

    }

    ChatInteractor(String chatId, String userId) {
        mTargetUserId = userId;
        mCurrentChat = mChatsTable.child(chatId);
        mCurrentChatMessages = mMessagesTable.child(chatId);
        mChatParticipants = new ArrayList<>();
    }

    @Override
    public void initChat(ChatStatusListener listener) {
        mChatStatusListener = listener;
        mChatListener = new ChatListener();
        mCurrentChat.addValueEventListener(mChatListener);
    }

    private void initUsers(final Chat chat) {
        mUserListener = new UserListener(chat);
        mUsersTable.addChildEventListener(mUserListener);
    }

    @Override
    public String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    @Override
    public List<UserAccount> getChatParticipants() {
        return mChatParticipants;
    }

    @Override
    public DatabaseReference getChatReference() {
        return mCurrentChatMessages;
    }

    @Override
    public void changeTitle(String new_title) {
        mCurrentChat.child(TITLE).setValue(new_title).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Trying to change chat title", e);
            }
        });
    }

    @Override
    public void destroyAllListeners() {
        destroyListener(mChatListener);
        destroyListener(mUserListener);
    }

    @Override
    public void sendMessage(Message chatMessage) {
        mCurrentChatMessages.push().setValue(chatMessage);
    }


    private class UserListener implements ChildEventListener {
        private final Chat chat;
        private String targetUserName;

        UserListener(Chat chat) {
            this.chat = chat;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
            if (Util.equals(userAccount.getId(), mTargetUserId)) {
                targetUserName = userAccount.getName();
            }
            if (chat.getUserIds().contains(userAccount.getId())) {
                mChatParticipants.add(userAccount);
            }
            if (chat.getUserIds().size() == mChatParticipants.size()) {
                mChatStatusListener.initComplete(mChatParticipants, targetUserName);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private class ChatListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Chat chat = dataSnapshot.getValue(Chat.class);
            initUsers(chat);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, databaseError.getMessage());
        }
    }
}
