package com.evilcorp.firebaseintegration.ui.chat;


import android.content.Context;

import com.evilcorp.firebaseintegration.data.firebase.model.Message;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;
import com.evilcorp.firebaseintegration.ui.base.BaseContract;
import com.google.firebase.database.DatabaseReference;

import java.util.List;


public interface ChatContract {

    interface View extends BaseContract.View {

        List<UserAccount> getChatParticipants();

        Context getContext();

        String getUserId();

        void setupRecyclerView(List<UserAccount> chatParticipants);

        void setupToolbar(String targetUserName);

    }

    interface Presenter extends BaseContract.Presenter {

        void changeTitle(String new_title);

        String getUserId();

        List<UserAccount> getChatParticipants();

        DatabaseReference getChatReference();

        void sendMessage(String message, String uId);

    }

    interface Interactor extends BaseContract.Interactor {

        String getUserId();

        List<UserAccount> getChatParticipants();

        DatabaseReference getChatReference();

        void changeTitle(String new_title);

        void sendMessage(Message chatMessage);

        void initChat(ChatInteractor.ChatStatusListener listener);
    }
}
