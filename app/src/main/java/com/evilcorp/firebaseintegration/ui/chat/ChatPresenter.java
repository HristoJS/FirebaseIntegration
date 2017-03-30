package com.evilcorp.firebaseintegration.ui.chat;

import com.evilcorp.firebaseintegration.data.firebase.model.Message;
import com.evilcorp.firebaseintegration.data.firebase.model.UserAccount;
import com.google.firebase.database.DatabaseReference;

import java.util.List;


class ChatPresenter implements ChatContract.Presenter, ChatInteractor.ChatStatusListener {
    private final ChatContract.View mChatView;
    private final ChatInteractor mChatInteractor;

    ChatPresenter(ChatContract.View view, String chatId, String userId) {
        this.mChatView = view;
        mChatInteractor = new ChatInteractor(chatId, userId, this);
    }

    @Override
    public List<UserAccount> getChatParticipants() {
        return mChatInteractor.getChatParticipants();
    }

    @Override
    public void changeTitle(String new_title) {
        mChatInteractor.changeTitle(new_title);
    }

    @Override
    public String getUserId() {
        return mChatInteractor.getUserId();
    }

    @Override
    public DatabaseReference getChatReference() {
        return mChatInteractor.getChatReference();
    }

    @Override
    public void sendMessage(String message, String uId, long date) {
        Message chatMessage = new Message(message, uId, date);
        mChatInteractor.sendMessage(chatMessage);
    }

    @Override
    public void destroy() {
        mChatInteractor.destroyListeners();
    }

    @Override
    public void initComplete(List<UserAccount> chatParticipants, String targetUserName) {
        mChatView.setupRecyclerView(chatParticipants);
        mChatView.setupToolbar(targetUserName);
    }
}
