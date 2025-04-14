package com.Main.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class User {

    private int UserId;

    private String UserEmail;

    @JsonIgnore
    private String UserPwd;
    private String UserName;
    private String avatarUrl;
    private String Location;
    private boolean Member;

    private long CreateTime;

    public int getId() {
        return UserId;
    }

    public void setId(int UserId) {
        this.UserId = UserId;
    }

    public long getCreatedAt() {
        return CreateTime;
    }

    public void setCreatedAt(long createdAt) {
        this.CreateTime = createdAt;
    }

    public String getCreatedDateTime() {
        return Instant.ofEpochMilli(this.CreateTime).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String getAvatarUrl(){
        if(avatarUrl == null){
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] hash = md.digest(this.UserEmail.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
                return "https://www.gravatar.com/avatar/" + String.format("%032x", new BigInteger(1, hash));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }else{
            return avatarUrl;
        }
    }
    public void setAvatarUrl(String avatarUrl){
        this.avatarUrl = avatarUrl;
    }


    public String getEmail() {
        return UserEmail;
    }

    public void setEmail(String UserEmail) {
        this.UserEmail = UserEmail;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getPassword() {
        return UserPwd;
    }

    public void setPassword(String UserPwd) {
        this.UserPwd = UserPwd;
    }

    public String getName() {
        return UserName;
    }

    public void setName(String UserName) {
        this.UserName = UserName;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, email='%s', name='%s', password='%s', createdAt=%d, createdDateTime='%s', location='%s', member=%b]",
                getId(), getEmail(), getName(), getPassword(), getCreatedAt(), getCreatedDateTime(), getLocation(), isMember());
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String userAddress) {
        Location = userAddress;
    }

    public boolean isMember() {
        return Member;
    }

    public void setMember(boolean member) {
        Member = member;
    }
}
