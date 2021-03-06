package com.ns.networking.model.guestrequest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GuestPrefsRequest {

    @SerializedName("token")
    private String token;
    @SerializedName("edit")
    private List<GuestPrefsDataRequest> editGuestPrefsRequestList ;
    @SerializedName("add")
    private List<AddGuestPrefsDataRequest> addGuestPrefsRequestList ;

    public List<GuestPrefsDataRequest> getEditGuestPrefsRequestList() {
        return editGuestPrefsRequestList;
    }

    public void setEditGuestPrefsRequestList(List<GuestPrefsDataRequest> editGuestPrefsRequestList) {
        this.editGuestPrefsRequestList = editGuestPrefsRequestList;
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
