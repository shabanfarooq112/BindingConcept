package com.example.bindingconcept.models;

import java.io.Serializable;
import java.util.List;

public class ChatRoot implements Serializable {

    public List<MembersInfo> membersInfo;
    public List<Integer> members;
    public int createdBy;
    public String title;
    public String lastMessage;
    public int senderId;
    public Long sentAt;
    public String senderName;
    public Long createdAt;
    public String id;

    public ChatRoot(){

    }

    public Long getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getCreatedBy() {
        return createdBy;
    }


    public List<MembersInfo> getMembersInfo() {
        return membersInfo;
    }

    public List<Integer> getMembers() {
        return members;
    }

    public int getCreatedBy(int i) {
        return createdBy;
    }

    public String getTitle() {
        return title;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getSenderId() {
        return senderId;
    }

    public Long getSentAt() {
        return sentAt;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setMembersInfo(List<MembersInfo> membersInfo) {
        this.membersInfo = membersInfo;
    }

    public void setMembers(List<Integer> members) {
        this.members = members;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setSentAt(Long sentAt) {
        this.sentAt = sentAt;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }


}
