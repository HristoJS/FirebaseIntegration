package com.evilcorp.firebaseintegration.chat;


import android.content.Context;

import com.evilcorp.firebaseintegration.model.firebase.UserAccount;
import com.google.firebase.database.DatabaseReference;

import java.util.List;


public interface ChatContract {

    interface View {

        List<UserAccount> getChatParticipants();

        Context getContext();

        String getUserId();

        void setupRecyclerView(List<UserAccount> chatParticipants);

        void setupToolbar(String targetUserName);

    }

    interface Presenter {

        void destroy();

        void changeTitle(String new_title);

        String getUserId();

        List<UserAccount> getChatParticipants();

        DatabaseReference getChatReference();

        void sendMessage(String message, String uId, long date);

    }
}
