package com.example.jresource.config.security;

import com.example.jresource.CustomSpringTokenIntrospection;
import com.example.jresource.OAuth2ResourceOpaqueProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class ResourceServerConfig {

    private final OAuth2ResourceOpaqueProperties resourceProperties;
    private final MappingJackson2HttpMessageConverter messageConverter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(sessionConfig ->
                                   sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize ->
                                       authorize
                                           .requestMatchers("/v3/api-docs").permitAll()
                                           .anyRequest().authenticated()
            );

        http.oauth2ResourceServer(
            configurer -> {
                configurer.opaqueToken(customizer -> {
                    customizer.introspector(new CustomSpringTokenIntrospection(
                        resourceProperties.getIntrospectionUri(),
                        resourceProperties.getClientId(),
                        resourceProperties.getClientSecret(),
                        messageConverter
                    ));
                });
            }
        );
        return http.build();
    }
}
