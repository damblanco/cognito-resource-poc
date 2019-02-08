package com.damian.cognitoresourcepoc.controller;

import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

/**
 * Basic functionality for Controller Testing.
 */
@RunWith(SpringRunner.class)
@AutoConfigureWebMvc
@AutoConfigureMockMvc(secure = false)
public abstract class AbstracControllerTest {

    @Autowired
    private MockMvc mockMvc;


    protected MockMvcRequestSpecification givenController() {
        return given().
                mockMvc(mockMvc);
    }

}