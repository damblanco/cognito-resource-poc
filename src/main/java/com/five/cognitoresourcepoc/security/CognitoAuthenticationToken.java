package com.five.cognitoresourcepoc.security;

import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class CognitoAuthenticationToken extends AbstractAuthenticationToken {

    private String token;
    private JWTClaimsSet details;
    private List<GrantedAuthority> authorities;

    public CognitoAuthenticationToken(Collection<? extends GrantedAuthority> authorities, String token , JWTClaimsSet details) {
        super(authorities);
        this.token = token;
        setDetails(details);
        setAuthenticated(true);
    }


    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return details;
    }
}
