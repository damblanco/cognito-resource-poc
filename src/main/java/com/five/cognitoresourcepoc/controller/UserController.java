package com.five.cognitoresourcepoc.controller;

import com.five.cognitoresourcepoc.security.dto.TokenClaims;
import com.five.cognitoresourcepoc.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    AuthService authService;

    /**
     * Get user's token claims
     */
    @GetMapping("/me")
    public TokenClaims getCurrentUser() {
        return authService.getClaims();
    }


}
