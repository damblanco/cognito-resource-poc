package com.damian.cognitoresourcepoc.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static com.damian.cognitoresourcepoc.security.enums.SecurityRole.USER_ROLE;
import static com.damian.cognitoresourcepoc.security.utils.AuthUtils.AUTHORIZATION_HEADER_PARAM_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link AuthFilter}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthFilterTest {

    private static final String JWT_MOCK = "jwtMock";
    private static final String AUTH_HEADER_PREFIX_VALUE = "Bearer ";
    private static final String AUTH_HEADER_FULL_VALUE = AUTH_HEADER_PREFIX_VALUE.concat(JWT_MOCK);
    private static final String ACCESS_DENIED_MESSAGE = "Access denied";

    @Mock
    private ConfigurableJWTProcessor<SecurityContext> processor;

    @InjectMocks
    private AuthFilter authFilter;

    /**
     * Test add authentication with valid JWT.
     */
    @Test
    public void testAddAuthenticationWithValidJWT() throws ServletException, IOException {

        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION_HEADER_PARAM_KEY, AUTH_HEADER_FULL_VALUE);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        //when
        authFilter.doFilter(request, response, filterChain);

        //then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNotNull();
        assertThat(authentication.getCredentials()).isEqualTo(JWT_MOCK);
        assertThat(((List) authentication.getAuthorities()).get(0).toString()).isEqualTo(USER_ROLE.getDescription());
    }

    /**
     * Test should throw {@link AccessDeniedException} with invalid JWT.
     */
    public void testThrowAccessDeniedExceptionWithInvalidJWT() throws ServletException, IOException, ParseException, JOSEException, BadJOSEException {

        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION_HEADER_PARAM_KEY, AUTH_HEADER_FULL_VALUE);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        doThrow(new ParseException("Parse Error", 1)).when(processor).process(JWT_MOCK, null);

        //when
        authFilter.doFilter(request, response, filterChain);

        //then
        assertThat(response.getContentAsString()).isEqualTo(ACCESS_DENIED_MESSAGE);
    }
}
