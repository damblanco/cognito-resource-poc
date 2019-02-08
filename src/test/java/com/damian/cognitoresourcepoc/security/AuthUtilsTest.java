package com.damian.cognitoresourcepoc.security;

import com.damian.cognitoresourcepoc.security.dto.TokenClaims;
import com.damian.cognitoresourcepoc.security.utils.AuthUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.damian.cognitoresourcepoc.security.utils.AuthUtils.AUTHORIZATION_HEADER_PARAM_KEY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AuthUtils}.
 */
public class AuthUtilsTest {

    private static final String GRANT_TYPE_PARAM_KEY = "grant_type";
    private static final String GRANT_TYPE_PARAM_VALUE = "authorization_code";
    private static final String CLIENT_ID_PARAM_KEY = "client_id";
    private static final String CLIENT_SECRET_PARAM_KEY = "client_secret";
    private static final String CODE_PARAM_KEY = "code";
    private static final String REDIRECT_URI_PARAM_KEY = "redirect_uri";
    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_SECRET = "secret";
    private static final String AUTHORIZATION_CODE = "authCode";
    private static final String CALLBACK_URL = "htttp://callback.com";
    private static final String CONTENT_TYPE_HEADER_PARAM_KEY = "Content-Type";
    private static final String BLANK_SPACE = " ";
    private static final String AUTH_TIME_TOKEN_CLAIM = "auth_time";
    private static final String COGNITO_USERNAME_TOKEN_CLAIM = "cognito:username";
    private static final String COGNITO_ROLES_TOKEN_CLAIM = "cognito:roles";
    private static final String EMAIL_TOKEN_CLAIM = "email";

    /**
     * Test for the Authorization Request creation.
     */
    @Test
    public void testBuildAuthorizationRequest() {

        //when
        HttpEntity<MultiValueMap<String, String>> request = AuthUtils.buildAuthorizationRequest(CLIENT_ID, CLIENT_SECRET, AUTHORIZATION_CODE, CALLBACK_URL);

        //then
        assertThat(request).isNotNull();
        assertThat(request.getHeaders()).isNotNull();
        assertThat(request.getHeaders().containsKey(AUTHORIZATION_HEADER_PARAM_KEY));
        assertThat(request.getHeaders().get(AUTHORIZATION_HEADER_PARAM_KEY).get(0).split(BLANK_SPACE).length == 2);
        assertThat(new String(Base64.decodeBase64(request.getHeaders().get(AUTHORIZATION_HEADER_PARAM_KEY).get(0).split(BLANK_SPACE)[1]))).isEqualTo(CLIENT_ID.concat(":").concat(CLIENT_SECRET));
        assertThat(request.getHeaders().containsKey(CONTENT_TYPE_HEADER_PARAM_KEY));
        assertThat(request.getHeaders().get(CONTENT_TYPE_HEADER_PARAM_KEY).get(0)).isEqualTo(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        MultiValueMap<String, String> bodyReq = request.getBody();

        assertThat(bodyReq).isNotNull();
        assertThat(bodyReq.get(GRANT_TYPE_PARAM_KEY).get(0)).isEqualTo(GRANT_TYPE_PARAM_VALUE);
        assertThat(bodyReq.get(CLIENT_ID_PARAM_KEY).get(0)).isEqualTo(CLIENT_ID);
        assertThat(bodyReq.get(CLIENT_SECRET_PARAM_KEY).get(0)).isEqualTo(CLIENT_SECRET);
        assertThat(bodyReq.get(CODE_PARAM_KEY).get(0)).isEqualTo(AUTHORIZATION_CODE);
        assertThat(bodyReq.get(REDIRECT_URI_PARAM_KEY).get(0)).isEqualTo(CALLBACK_URL);
    }

    /**
     * Test for the token claims mapping.
     */
    @Test
    public void testBuildTokenClaims() throws ParseException {

        //given
        Long authTimeClaim = 1L;
        String subject = "subject";
        String cognitoUser = "username";
        List<String> roles = Collections.singletonList("role");
        String email = "user@email.com";
        Date time = new Date();

        JWTClaimsSet details = new JWTClaimsSet.Builder()
                .subject(subject)
                .claim(AUTH_TIME_TOKEN_CLAIM, authTimeClaim)
                .issueTime(time)
                .expirationTime(time)
                .claim(COGNITO_USERNAME_TOKEN_CLAIM, cognitoUser)
                .claim(COGNITO_ROLES_TOKEN_CLAIM, roles)
                .claim(EMAIL_TOKEN_CLAIM, email)
                .build();

        //when
        TokenClaims tokenClaims = AuthUtils.buildTokenClaims(details);

        //then
        assertThat(tokenClaims).isNotNull();
        assertThat(tokenClaims.getUuid()).isEqualTo(subject);
        assertThat(tokenClaims.getAuthTime()).isEqualTo(authTimeClaim);
        assertThat(tokenClaims.getIssued()).isEqualTo(time);
        assertThat(tokenClaims.getExpire()).isEqualTo(time);
        assertThat(tokenClaims.getCognitoUserName()).isEqualTo(cognitoUser);
        assertThat(tokenClaims.getCognitoRoles()).isEqualTo(roles);
        assertThat(tokenClaims.getEmail()).isEqualTo(email);
    }

    @Test(expected = ParseException.class)
    public void testBuildTokenClaimsShouldThrowParseException() throws ParseException {

        //given
        String roles = "role";

        JWTClaimsSet details = new JWTClaimsSet.Builder()
                .claim(COGNITO_ROLES_TOKEN_CLAIM, roles)
                .build();

        //when
        AuthUtils.buildTokenClaims(details);
    }

}
