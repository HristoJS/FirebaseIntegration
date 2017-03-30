package com.evilcorp.firebaseintegration.data.firebase.model;

import java.util.List;

/**
 * Chat Model
 */

public class Chat {
    private String chatId;
    private String lastMessageId;
    private String title;
    private List<String> userIds;

    public Chat(){

    }

    public Chat(String chatId, String lastMessageId, String title, List<String> userIds) {
        this.chatId = chatId;
        this.lastMessageId = lastMessageId;
        this.title = title;
        this.userIds = userIds;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
