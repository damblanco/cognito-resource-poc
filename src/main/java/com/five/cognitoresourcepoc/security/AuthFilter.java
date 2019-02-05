package com.five.cognitoresourcepoc.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Optional;

import static com.five.cognitoresourcepoc.utils.AuthUtils.AUTHORIZATION_HEADER_PARAM_KEY;
import static com.five.cognitoresourcepoc.utils.AuthUtils.USER_ROLE;
import static java.util.Objects.isNull;

@Slf4j
@Component
@Order(1)
public class AuthFilter extends GenericFilterBean {

    private static final String AUTH_HEADER_PREFIX_VALUE = "Bearer ";

    @Autowired
    private ConfigurableJWTProcessor<SecurityContext> processor;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) req;
            String token = extractToken(request.getHeader(AUTHORIZATION_HEADER_PARAM_KEY));
            CognitoAuthenticationToken authentication = extractAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(req, res);
        } catch (AccessDeniedException e) {
            log.error("Access denied: " + Optional.ofNullable(e).map(AccessDeniedException::getMessage).orElse("No Message"));
            HttpServletResponse response = (HttpServletResponse) res;
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Access denied");
        }
    }

    /**
     * Extract token from header
     */
    private String extractToken(String header) {
        return Optional.ofNullable(header)
                .map(h -> h.split(AUTH_HEADER_PREFIX_VALUE))
                .map(splitAuthHeader -> splitAuthHeader.length > 1 ? splitAuthHeader[1] : null)
                .orElse(null);

    }

    /**
     * Extract authentication details from token
     */
    private CognitoAuthenticationToken extractAuthentication(String token) throws AccessDeniedException {
        if (isNull(token))
            return null;

        try {
            JWTClaimsSet claims = processor.process(token, null);
            return new CognitoAuthenticationToken(Collections.singletonList(new SimpleGrantedAuthority(USER_ROLE)), token, claims);
        } catch (ParseException | BadJOSEException | JOSEException e) {
            throw new AccessDeniedException(Optional.ofNullable(e).map(Exception::getMessage).orElse("No Message"));
        }
    }

}
