package com.businesschat;

public class BusinessProfile {
    private int id;
    private String name;
    private String category;
    private String chatLink;

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getChatLink() {
        return chatLink;
    }
    public void setChatLink(String chatLink) {
        this.chatLink = chatLink;
    }
}
