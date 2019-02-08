package com.damian.cognitoresourcepoc.controller;

import com.damian.cognitoresourcepoc.service.AuthService;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AuthController}.
 */
@SpringBootTest(classes = AuthController.class)
public class AuthControllerTest extends AbstracControllerTest {

    private final static String CODE_PARAM = "code";
    private final static String CODE_VALUE = "codeValue";

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