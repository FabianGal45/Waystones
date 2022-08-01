package eu.ovmc.waystones;

import java.util.UUID;

public class User {

    private String uuid;
    private String userName;
    private int privateWs;
    private int publicWs;

    public User() {
        this.uuid = null;
        this.userName = "";
        this.privateWs = 0;
        this.publicWs = 0;
    }

    public User(String uuid, String userName, int privateWs, int publicWs) {
        this.uuid = uuid;
        this.userName = userName;
        this.privateWs = privateWs;
        this.publicWs = publicWs;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPrivateWs() {
        return privateWs;
    }

    public void setPrivateWs(int privateWs) {
        this.privateWs = privateWs;
    }

    public int getPublicWs() {
        return publicWs;
    }

    public void setPublicWs(int publicWs) {
        this.publicWs = publicWs;
    }
}
