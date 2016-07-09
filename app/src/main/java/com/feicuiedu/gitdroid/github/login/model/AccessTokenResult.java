package com.feicuiedu.gitdroid.github.login.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/7/6 0006.
 */
public class AccessTokenResult {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("scope")
    private String scope;

    public String getAccessToken() {
        return accessToken;
    }

    public String getScope() {
        return scope;
    }

    public String getTokenType() {
        return tokenType;
    }
}
