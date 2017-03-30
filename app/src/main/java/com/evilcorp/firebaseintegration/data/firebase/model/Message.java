package com.evilcorp.firebaseintegration.data.firebase.model;


/**
 * Chat Message Model
 */

public class Message {
    private String id;
    private String message;
    private String userId;
    private long timestamp;

    public Message(){}

    public Message(String message, String userId, long timestamp ){
        this.message = message;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
