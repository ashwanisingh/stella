package com.ns.networking.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddGuestPrefsRequest {

    @SerializedName("token")
    private String token;
    @SerializedName("user")
    private String userId;
    @SerializedName("add")
    private List<AddGuestPrefsDataRequest> addGuestPrefsRequestList ;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<AddGuestPrefsDataRequest> getAddGuestPrefsRequestList() {
        return addGuestPrefsRequestList;
    }

    public void setAddGuestPrefsRequestList(List<AddGuestPrefsDataRequest> addGuestPrefsRequestList) {
        this.addGuestPrefsRequestList = addGuestPrefsRequestList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
