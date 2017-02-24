package com.evilcorp.firebaseintegration.chat;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evilcorp.firebaseintegration.model.firebase.Chat;
import com.evilcorp.firebaseintegration.model.firebase.Message;
import com.evilcorp.firebaseintegration.model.firebase.UserAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hristo.stoyanov on 15-Feb-17.
 */

class ChatInteractor {
    private static final String TAG = ChatInteractor.class.getSimpleName();
    private static final String USERS = "users";
    private static final String MESSAGES = "messages";
    private static final String CHATS = "chats";

    private DatabaseReference mDatabase;
    private DatabaseReference mChats;
    private DatabaseReference mUsers;

    private String chatId;
    private List<UserAccount> chatParticipants;
    private ChatListener mChatListener;
    private UserListener mUserListener;
    private ChatStatusListener mChatStatusListener;
    private String targetUserId;

    public interface ChatStatusListener{
        void initComplete(List<UserAccount> chatParticipants, String targetUserName);
        void initFailed();
    }

    ChatInteractor(String chatId,String userId,ChatStatusListener listener){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.chatId = chatId;
        mChats = getDBReference(CHATS).child(chatId);
        mUsers = getDBReference(USERS);
        this.mChatStatusListener = listener;
        this.targetUserId = userId;
        initChat();
        chatParticipants = new ArrayList<>();
    }

    private void initChat(){
        mChatListener = new ChatListener();
        mChats.addValueEventListener(mChatListener);
    }

    private void initUsers(final Chat chat){
        mUserListener = new UserListener(chat);
        mUsers.addChildEventListener(mUserListener);
    }


    DatabaseReference getChatReference() {
        return getDBReference(MESSAGES).child(chatId);
    }

    String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null)? user.getUid() : null;
    }

    List<UserAccount> getChatParticipants() {
        return chatParticipants;
    }

    void changeTitle(String new_title) {
        getDBReference(CHATS).child(chatId).child("title").setValue(new_title).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    void destroyListeners(){
        if(mChatListener != null) {
            mChats.removeEventListener(mChatListener);
            mChatListener = null;
        }
        if(mUserListener != null) {
            mUsers.removeEventListener(mUserListener);
            mUserListener = null;
        }
    }

    void sendMessage(Message chatMessage) {
        getChatReference().push().setValue(chatMessage);
    }

    private DatabaseReference getDBReference(String tableId){
        return mDatabase.child(tableId);
    }

    private class UserListener implements ChildEventListener{
        private Chat chat;
        private String targetUserName;
        UserListener(Chat chat){
            this.chat = chat;
        }
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
            if(userAccount.getId().equals(targetUserId)){
                targetUserName = userAccount.getName();
            }
            if(chat.getUserIds().contains(userAccount.getId())) {
                chatParticipants.add(userAccount);
            }
            if(chat.getUserIds().size() == chatParticipants.size()){
                mChatStatusListener.initComplete(chatParticipants,targetUserName);
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

    private class ChatListener implements ValueEventListener{
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Chat chat = dataSnapshot.getValue(Chat.class);
            initUsers(chat);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG,databaseError.getMessage());
        }
    }
}
