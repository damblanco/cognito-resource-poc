package com.five.cognitoresourcepoc.service;

import com.five.cognitoresourcepoc.security.dto.CognitoJWT;
import com.five.cognitoresourcepoc.security.dto.TokenClaims;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;

import static com.five.cognitoresourcepoc.utils.AuthUtils.buildAuthorizationRequest;
import static com.five.cognitoresourcepoc.utils.AuthUtils.buildTokenClaims;

@Service
@Slf4j
public class AuthService {
    @Value("${endpoints.token}")
    private String tokenUrl;
    @Value("${cognito.client}")
    private String clientId;
    @Value("${cognito.secret}")
    private String clientSecret;
    @Value("${cognito.callback}")
    private String callbackUrl;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Get token with authorization code.
     */
    public CognitoJWT getToken(final String code) {
        try {
            ResponseEntity<CognitoJWT> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, buildAuthorizationRequest(clientId, clientSecret, code, callbackUrl), CognitoJWT.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new HttpServerErrorException(e.getStatusCode(), e.getMessage());
        }
    }


    /**
     * Get User Token Claims.
     */
    public TokenClaims getClaims() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JWTClaimsSet details = (JWTClaimsSet) (authentication.getDetails());
        try {
            return buildTokenClaims(details);
        } catch (ParseException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error");
        }
    }


}
