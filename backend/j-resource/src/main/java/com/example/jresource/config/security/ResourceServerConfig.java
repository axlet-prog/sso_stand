package com.example.jresource.config.security;

import com.example.jresource.CustomSpringTokenIntrospection;
import com.example.jresource.OAuth2ResourceOpaqueProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class ResourceServerConfig {

	private final OAuth2ResourceOpaqueProperties resourceProperties;
	private final JsonMapper jsonMapper;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.sessionManagement(sessionConfig ->
						sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorize ->
						authorize
								.requestMatchers("/v3/api-docs").permitAll()
								.requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
								.anyRequest().authenticated()
				);

		http.oauth2ResourceServer(
				configurer -> {
					configurer.opaqueToken(customizer -> {
						customizer.introspector(new CustomSpringTokenIntrospection(
								resourceProperties.getIntrospectionUri(),
								resourceProperties.getClientId(),
								resourceProperties.getClientSecret(),
								new JacksonJsonHttpMessageConverter(jsonMapper)
						));
					});
				}
		);
		return http.build();
	}
}
