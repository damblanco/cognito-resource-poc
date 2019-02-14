package com.damian.cognitoresourcepoc.service;

import com.damian.cognitoresourcepoc.security.dto.CognitoJWT;
import com.damian.cognitoresourcepoc.security.dto.TokenClaims;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuthService}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    private static final String TOKEN_URL_FIELD = "tokenUrl";
    private static final String CLIENT_ID_FIELD = "clientId";
    private static final String CLIENT_SECRET_FIELD = "clientSecret";
    private static final String CALLBACK_URL_FIELD = "callbackUrl";
    private static final String TOKEN_URL = "http://authtoken.com";
    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_SECRET = "secret";
    private static final String CALLBACK_URL = "htttp://callback.com";
    private static final String AUTHORIZATION_CODE = "authCode";
    private static final String ID_TOKEN = "token";
    private static final String CLIENT_ID_PARAM_KEY = "client_id";
    private static final String CLIENT_SECRET_PARAM_KEY = "client_secret";
    private static final String REDIRECT_URI_PARAM_KEY = "redirect_uri";
    private static final String CODE_PARAM_KEY = "code";
    private static final String COGNITO_ROLES_TOKEN_CLAIM = "cognito:roles";



    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthService authService;


    @Before
    public void setUp() {
        ReflectionTestUtils.setField(authService, TOKEN_URL_FIELD, TOKEN_URL);
        ReflectionTestUtils.setField(authService, CLIENT_ID_FIELD, CLIENT_ID);
        ReflectionTestUtils.setField(authService, CLIENT_SECRET_FIELD, CLIENT_SECRET);
        ReflectionTestUtils.setField(authService, CALLBACK_URL_FIELD, CALLBACK_URL);
    }

    /**
     * Test authorization request.
     */
    @Test
    public void testAuthorizationRequest() {

        //given
        CognitoJWT jwt = new CognitoJWT();
        jwt.setIdToken(ID_TOKEN);

        ArgumentCaptor<String> tokenUrlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<HttpMethod> htttpMethodCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(new ResponseEntity<>(jwt, HttpStatus.OK));

        //when
        CognitoJWT result = authService.getToken(AUTHORIZATION_CODE);

        //then
        assertThat(result)
                .isNotNull()
                .matches(token -> ID_TOKEN.equals(token.getIdToken()));

        verify(restTemplate).exchange(tokenUrlCaptor.capture(), htttpMethodCaptor.capture(), requestCaptor.capture(), classCaptor.capture());

        assertThat(tokenUrlCaptor.getValue())
                .isNotNull()
                .isEqualTo(TOKEN_URL);
        assertThat(htttpMethodCaptor.getValue())
                .isNotNull()
                .isEqualTo(HttpMethod.POST);
        assertThat(classCaptor.getValue())
                .isNotNull()
                .isEqualTo(CognitoJWT.class);
        assertThat(requestCaptor.getValue().getBody().get(CLIENT_ID_PARAM_KEY).get(0)).isEqualTo(CLIENT_ID);
        assertThat(requestCaptor.getValue().getBody().get(CLIENT_SECRET_PARAM_KEY).get(0)).isEqualTo(CLIENT_SECRET);
        assertThat(requestCaptor.getValue().getBody().get(REDIRECT_URI_PARAM_KEY).get(0)).isEqualTo(CALLBACK_URL);
        assertThat(requestCaptor.getValue().getBody().get(CODE_PARAM_KEY).get(0)).isEqualTo(AUTHORIZATION_CODE);
    }

    /**
     * Test get Token Claims.
     */
    @Test
    public void testGetClaims() {

        //given
        Date time = new Date();
        JWTClaimsSet details = new JWTClaimsSet.Builder()
                .issueTime(time)
                .build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getDetails()).thenReturn(details);

        //when
        TokenClaims tokenClaims = authService.getClaims();

        //then
        assertThat(tokenClaims).isNotNull();
        assertThat(tokenClaims.getIssued()).isEqualTo(time);
    }

    /**
     * Test get Token Claims should throw {@link HttpServerErrorException} due to {@link ParseException}.
     */
    @Test(expected = HttpServerErrorException.class)
    public void testGetClaimsShouldThrowHttpServerErrorException() {

        //given
        String roles = "role";

        JWTClaimsSet details = new JWTClaimsSet.Builder()
                .claim(COGNITO_ROLES_TOKEN_CLAIM, roles)
                .build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getDetails()).thenReturn(details);

        //when
        authService.getClaims();
    }
}

