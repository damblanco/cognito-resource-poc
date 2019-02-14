package com.damian.cognitoresourcepoc.controller;

import com.damian.cognitoresourcepoc.service.AuthService;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AuthController}.
 */
@SpringBootTest(classes = AuthController.class)
public class AuthControllerTest extends AbstracControllerTest {

    private static final String CODE_PARAM = "code";
    private static final String CODE_VALUE = "codeValue";

    @MockBean
    private AuthService authService;

    /**
     * Test for /auth/token endpoint.
     */
    @Test
    public void testCallTokenEndpoint() {

        //when
        givenController()
                .param(CODE_PARAM, CODE_VALUE)
                .when()
                .get("/auth/token")
                .then()
                .statusCode(200);


        //then
        verify(authService, times(1)).getToken(CODE_VALUE);
    }
}