package com.damian.cognitoresourcepoc.security.utils;

import com.damian.cognitoresourcepoc.security.dto.TokenClaims;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.ParseException;
import java.util.Collections;

public final class AuthUtils {

    public static final String AUTHORIZATION_HEADER_PARAM_KEY = "Authorization";
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

    private AuthUtils() {
    }


    /**
     * Build oauth/token request.
     *
     * @param clientId     authorization provider client id.
     * @param clientSecret authorization provider secret.
     * @param code         obtained from login redirect.
     * @param callbackUrl  to redirect after login.
     * @return {@link HttpEntity}
     */
    public static HttpEntity<MultiValueMap<String, String>> buildAuthorizationRequest(final String clientId, final String clientSecret, final String code, final String callbackUrl) {
        byte[] auth = Base64.encodeBase64(clientId.concat(":").concat(clientSecret).getBytes());

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.put(AUTHORIZATION_HEADER_PARAM_KEY, Collections.singletonList("Basic " + new String(auth)));
        headers.put(CONTENT_TYPE_HEADER_PARAM_KEY, Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED_VALUE));

        return (HttpEntity<MultiValueMap<String, String>>) new HttpEntity(buildAuthorizationRequestBody(clientId, clientSecret, code, callbackUrl), headers);
    }


    /**
     * Build oauth/token body request.
     *
     * @param clientId     authorization provider client id.
     * @param clientSecret authorization provider secret.
     * @param code         obtained from login redirect.
     * @param callbackUrl  to redirect after login.
     * @return {@link MultiValueMap}
     */
    private static MultiValueMap<String, String> buildAuthorizationRequestBody(final String clientId, final String clientSecret, final String code, final String callbackUrl) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(GRANT_TYPE_PARAM_KEY, GRANT_TYPE_PARAM_VALUE);
        body.add(CLIENT_ID_PARAM_KEY, clientId);
        body.add(CLIENT_SECRET_PARAM_KEY, clientSecret);
        body.add(CODE_PARAM_KEY, code);
        body.add(REDIRECT_URI_PARAM_KEY, callbackUrl);

        return body;
    }

    /**
     * Build {@link TokenClaims} object from {@link JWTClaimsSet} values.
     *
     * @param {@link JWTClaimsSet} set of jwt token claims
     * @return {@link TokenClaims}
     */
    public static TokenClaims buildTokenClaims(final JWTClaimsSet details) throws ParseException {
        return TokenClaims
                .builder()
                .uuid(details.getSubject())
                .authTime(details.getLongClaim(AUTH_TIME_TOKEN_CLAIM))
                .issued(details.getIssueTime())
                .expire(details.getExpirationTime())
                .cognitoUserName(details.getStringClaim(COGNITO_USERNAME_TOKEN_CLAIM))
                .cognitoRoles(details.getStringListClaim(COGNITO_ROLES_TOKEN_CLAIM))
                .email(details.getStringClaim(EMAIL_TOKEN_CLAIM))
                .build();
    }

}
