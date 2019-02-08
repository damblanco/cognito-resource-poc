package com.damian.cognitoresourcepoc.controller;

import com.damian.cognitoresourcepoc.service.AuthService;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link UserController}.
 */
@SpringBootTest(classes = UserController.class)
public class UserControllerTest extends AbstracControllerTest{

    @MockBean
    private AuthService authService;

    /**
     * Test for /user/me endpoint.
     */
    @Test
    public void testCallMeEndpoint() {

        //when
        givenController()
                .when()
                .get("/user/me")
                .then()
                .statusCode(200);


        //then
        verify(authService, times(1)).getClaims();
    }
}