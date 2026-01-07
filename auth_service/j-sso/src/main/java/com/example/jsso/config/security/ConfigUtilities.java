package com.example.jsso.config.security;

import com.example.jsso.config.properties.AuthorizationServerProperties;
import com.example.jsso.service.RedisOAuth2AuthorizationConsentService;
import com.example.jsso.service.RedisOAuth2AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class ConfigUtilities {

    private final AuthorizationServerProperties properties;

    @Bean
    public RedisTemplate<String, OAuth2Authorization> redisTemplateOAuth2AuthorizationService(
        RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, OAuth2Authorization> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, OAuth2AuthorizationConsent> redisTemplateOAuth2AuthorizationConsent(
        RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, OAuth2AuthorizationConsent> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisOAuth2AuthorizationConsentService oAuth2AuthorizationConsent(
        RedisTemplate<String, OAuth2AuthorizationConsent> redisTemplate
    ) {
        return new RedisOAuth2AuthorizationConsentService(redisTemplate, properties.getAuthorizationConsentTtlMs());
    }


    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(
        RedisTemplate<String, OAuth2Authorization> redisTemplate
    ) {
        return new RedisOAuth2AuthorizationService(redisTemplate, properties.getAuthorizationTtlMs());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
