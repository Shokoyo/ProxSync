package de.dieser1memesprech.proxsync.user;

public class User {
    private String uid;
    private String name;
    private String avatarUrl;


    public User(String uid, String name, String avatarUrl) {
        this.uid = uid;
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
