package com.evilcorp.firebaseintegration.chat;

import com.evilcorp.firebaseintegration.model.firebase.Message;
import com.evilcorp.firebaseintegration.model.firebase.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


class ChatPresenter implements ChatContract.Presenter, ChatInteractor.ChatStatusListener {
    private ChatContract.View chatView;
    private ChatInteractor interactor;

    ChatPresenter(ChatContract.View view, String chatId, String userId){
        this.chatView = view;
        interactor = new ChatInteractor(chatId,userId,this);
    }

    @Override
    public List<UserAccount> getChatParticipants() {
        return interactor.getChatParticipants();
    }

    @Override
    public void changeTitle(String new_title) {
        interactor.changeTitle(new_title);
    }

    @Override
    public String getUserId() {
        return interactor.getUserId();
    }

    @Override
    public DatabaseReference getChatReference() {
        return interactor.getChatReference();
    }

    @Override
    public void sendMessage(String message, String uId, long date) {
        Message chatMessage = new Message(message,uId,date);
        interactor.sendMessage(chatMessage);
    }

    @Override
    public void destroy(){
        interactor.destroyListeners();
    }

    @Override
    public void initComplete(List<UserAccount> chatParticipants, String targetUserName) {
        chatView.setupRecyclerView(chatParticipants);
        chatView.setupToolbar(targetUserName);
    }

    @Override
    public void initFailed() {

    }
}
