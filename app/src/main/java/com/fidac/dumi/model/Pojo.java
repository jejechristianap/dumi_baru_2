package com.fidac.dumi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.List;

public class Pojo {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("email")
    @Expose
    private String email;
    /*@SerializedName("user_array")
    @Expose
    private List<UserArray> userArray = null;*/

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*public List<UserArray> getUserArray() {
        return userArray;
    }

    public void setUserArray(List<UserArray> userArray) {
        this.userArray = userArray;
    }*/
}
