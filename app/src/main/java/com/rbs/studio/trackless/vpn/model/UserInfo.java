package com.rbs.studio.trackless.vpn.model;

public class UserInfo {
    String identity;
    String name;
    String email;

    public UserInfo() {
    }

    public UserInfo(String identity, String name, String email) {
        this.name = name;
        this.identity = identity;
        this.email  = email;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "identity='" + identity + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public String getIdentity() {return identity;}

    public void setIdentity(String identity) {this.identity = identity;}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
