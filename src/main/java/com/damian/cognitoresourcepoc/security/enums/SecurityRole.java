package com.damian.cognitoresourcepoc.security.enums;

public enum SecurityRole {

    USER_ROLE("USER_ROLE");

    private String description;


    SecurityRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
