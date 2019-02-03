package com.five.cognitoresourcepoc.security.dto;

import lombok.Data;
import lombok.experimental.Builder;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class TokenClaims {

    private String uuid;
    private long authTime;
    private Date issued;
    private Date expire;
    private String cognitoUserName;
    private String email;
    private List<String> cognitoRoles;

}
