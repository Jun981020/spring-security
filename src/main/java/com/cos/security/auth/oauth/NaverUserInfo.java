package com.cos.security.auth.oauth;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {
    private Map<String,Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String,Object> getResponse(){
        return (Map<String,Object>)attributes.get("response");
    }
    @Override
    public String getProvider() {
        return (String)getResponse().get("id");
    }

    @Override
    public String getProviderId() {
        return "naver";
    }

    @Override
    public String getName() {
        return (String) getResponse().get("name");
    }

    @Override
    public String getEmail() {
        return (String) getResponse().get("email");
    }
}
