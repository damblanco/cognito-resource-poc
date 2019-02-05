package com.five.cognitoresourcepoc.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
@Slf4j
public class JWTProcessorConfig {

    @Value("${cognito.keys}")
    private String keySource;

    @Bean
    public ConfigurableJWTProcessor configurableJWTProcessor() {
        DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever(5000, 5000);
        URL jwkSetURL = null;
        try {
            jwkSetURL = new URL(keySource);
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
        }

        JWKSource<SecurityContext> keySource = new RemoteJWKSet(jwkSetURL, resourceRetriever);
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor();
        JWSVerificationKeySelector keySelector = new JWSVerificationKeySelector(JWSAlgorithm.RS256, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
        return jwtProcessor;
    }

}
