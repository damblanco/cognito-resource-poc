package com.five.cognitoresourcepoc.controller;

import com.five.cognitoresourcepoc.security.dto.CognitoJWT;
import com.five.cognitoresourcepoc.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${endpoints.authorize}")
    private String authorizeUrl;

    @Autowired
    AuthService authService;

    /**
     * Redirect user to correct url for authorization code
     */
    @GetMapping("/login")
    public ResponseEntity<Object> login() {
        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, authorizeUrl)
                .build();
    }

    /**
     * Get aws tokens with authorization code
     */
    @GetMapping("/token")
    public CognitoJWT getToken(@RequestParam("code") String code) {
        return authService.getToken(code);
    }

}
