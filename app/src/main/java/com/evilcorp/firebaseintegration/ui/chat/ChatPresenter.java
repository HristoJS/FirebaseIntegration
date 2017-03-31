package com.evilcorp.firebaseintegration.ui.chat;

import com.evilcorp.firebaseintegration.data.firebase.model.Message;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;
import com.evilcorp.firebaseintegration.helper.Time;
import com.evilcorp.firebaseintegration.ui.base.BasePresenter;
import com.google.firebase.database.DatabaseReference;

import java.util.List;


class ChatPresenter extends BasePresenter implements ChatContract.Presenter, ChatInteractor.ChatStatusListener {
    private final ChatContract.View mChatView;
    private final ChatContract.Interactor mChatInteractor;

    ChatPresenter(ChatContract.View view, ChatContract.Interactor interactor) {
        super(view, interactor);
        this.mChatView = view;
        mChatInteractor = interactor;
        mChatInteractor.initChat(this);
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
    public void sendMessage(String message, String uId) {
        Message chatMessage = new Message(message, uId, Time.getTime());
        mChatInteractor.sendMessage(chatMessage);
    }

    @Override
    public void initComplete(List<UserAccount> chatParticipants, String targetUserName) {
        mChatView.setupRecyclerView(chatParticipants);
        mChatView.setupToolbar(targetUserName);
    }
}
