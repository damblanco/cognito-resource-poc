package com.five.cognitoresourcepoc.utils;

import com.five.cognitoresourcepoc.security.dto.TokenClaims;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;

public class AuthUtils {

    //User Logged Role
    public static final String USER_ROLE = "USER_ROLE";


    private static final String AUTHORIZATION_HEADER_PARAM_KEY = "Authorization";
    private static final String CONTENT_TYPE_HEADER_PARAM_KEY = "Content-Type";
    private static final String GRANT_TYPE_PARAM_KEY = "grant_type";
    private static final String GRANT_TYPE_PARAM_VALUE = "authorization_code";
    private static final String CLIENT_ID_PARAM_KEY = "client_id";
    private static final String CLIENT_SECRET_PARAM_KEY = "client_secret";
    private static final String CODE_PARAM_KEY = "code";
    private static final String REDIRECT_URI_PARAM_KEY = "redirect_uri";
    private static final String SUB_TOKEN_CLAIM = "sub";
    private static final String AUTH_TIME_TOKEN_CLAIM = "auth_time";
    private static final String IAT_TOKEN_CLAIM = "iat";
    private static final String EXP_TOKEN_CLAIM = "exp";
    private static final String COGNITO_USERNAME_TOKEN_CLAIM = "cognito:username";
    private static final String COGNITO_ROLES_TOKEN_CLAIM = "cognito:roles";
    private static final String EMAIL_TOKEN_CLAIM = "email";


    /**
     * Build oauth/token request
     *
     * @param clientId
     * @param clientSecret
     * @param code         obtained from login redirect
     * @param callbackUrl
     * @return {@link HttpEntity<MultiValueMap<String, String>>}
     */
    public static HttpEntity<MultiValueMap<String, String>> buildAuthorizationRequest(String clientId, String clientSecret, String code, String callbackUrl) {
        byte[] auth = Base64.encodeBase64(clientId.concat(":").concat(clientSecret).getBytes());

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.put(AUTHORIZATION_HEADER_PARAM_KEY, Collections.singletonList("Basic " + new String(auth)));
        headers.put(CONTENT_TYPE_HEADER_PARAM_KEY, Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED_VALUE));

        return (HttpEntity<MultiValueMap<String, String>>) new HttpEntity(buildAuthorizationRequestBody(clientId, clientSecret, code, callbackUrl), headers);
    }


    /**
     * Build oauth/token body request
     *
     * @param clientId
     * @param clientSecret
     * @param code         obtained from login redirect
     * @param callbackUrl
     * @return {@link MultiValueMap<String, String>}
     */
    private static MultiValueMap<String, String> buildAuthorizationRequestBody(String clientId, String clientSecret, String code, String callbackUrl) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(GRANT_TYPE_PARAM_KEY, GRANT_TYPE_PARAM_VALUE);
        body.add(CLIENT_ID_PARAM_KEY, clientId);
        body.add(CLIENT_SECRET_PARAM_KEY, clientSecret);
        body.add(CODE_PARAM_KEY, code);
        body.add(REDIRECT_URI_PARAM_KEY, callbackUrl);

        return body;
    }

    /**
     * Build {@link TokenClaims} object from {@link JWTClaimsSet} values
     *
     * @param {@link JWTClaimsSet} set of jwt token claims
     * @return {@link TokenClaims}
     */
    public static TokenClaims buildTokenClaims(JWTClaimsSet details) throws ParseException {
        return TokenClaims
                .builder()
                .uuid(details.getStringClaim(SUB_TOKEN_CLAIM))
                .authTime((Long) details.getClaim(AUTH_TIME_TOKEN_CLAIM))
                .issued((Date) details.getClaim(IAT_TOKEN_CLAIM))
                .expire((Date) details.getClaim(EXP_TOKEN_CLAIM))
                .cognitoUserName(details.getStringClaim(COGNITO_USERNAME_TOKEN_CLAIM))
                .cognitoRoles(details.getStringListClaim(COGNITO_ROLES_TOKEN_CLAIM))
                .email(details.getStringClaim(EMAIL_TOKEN_CLAIM))
                .build();
    }

}
