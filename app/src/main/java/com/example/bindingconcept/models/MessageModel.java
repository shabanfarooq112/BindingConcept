package com.example.bindingconcept.models;

public class MessageModel {
    public String attachment;
    public String message;
    public int senderId;
    public Long sentAt;
    public int attachmentType;
    public String id;

    public MessageModel(){

    }

    public MessageModel(String attachment, String message, int senderId, Long sentAt, int attachmentType , String id) {
        this.attachment = attachment;
        this.message = message;
        this.senderId = senderId;
        this.sentAt = sentAt;
        this.attachmentType = attachmentType;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAttachmentType(int attachmentType) {
        this.attachmentType = attachmentType;
    }

    public int getAttachmentType() {
        return attachmentType;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSentAt(Long sentAt) {
        this.sentAt = sentAt;
    }

    public String getAttachment() {
        return attachment;
    }

    public String getMessage() {
        return message;
    }


    public Long getSentAt() {
        return sentAt;
    }
}
