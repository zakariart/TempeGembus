package com.tempegembus.zakaria.tempegembus.Model;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private String type;
    private boolean isseen;
    private String msg_image;

    public Chat(String sender, String receiver, String message, String type, boolean isseen, String msg_image) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
        this.isseen = isseen;
        this.msg_image = msg_image;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getMsg_image() {
        return msg_image;
    }

    public void setMsg_image(String msg_image) {
        this.msg_image = msg_image;
    }

}