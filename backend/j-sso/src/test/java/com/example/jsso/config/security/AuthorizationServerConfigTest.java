package com.example.jsso.config.security;

import com.example.jsso.config.properties.AuthorizationServerProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.web.FilterChainProxy;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationServerConfigTest {

	private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class))
			.withUserConfiguration(SecurityTestConfiguration.class, AuthorizationServerConfig.class)
			.withBean(OAuth2AuthorizationService.class, InMemoryOAuth2AuthorizationService::new)
			.withBean(RegisteredClientRepository.class, () -> new InMemoryRegisteredClientRepository(
					RegisteredClient.withId("test-client")
							.clientId("test-client")
							.clientSecret("{noop}test-secret")
							.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
							.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
							.scope("read")
							.build()))
			.withBean(AuthorizationServerProperties.class, () -> {
				var properties = new AuthorizationServerProperties();
				properties.setIssuerUrl("https://sso.example.test");
				properties.setIntrospectionEndpoint("/oauth2/token-info");
				return properties;
			});

	@Test
	void startsWithAutoConfiguredJsonMapperWithoutConverterBean() {
		contextRunner.run(context -> assertThat(context)
				.hasNotFailed()
				.hasSingleBean(JsonMapper.class)
				.hasSingleBean(AuthorizationServerConfig.class)
				.hasBean("authServerSecurityFilterChain")
				.doesNotHaveBean(JacksonJsonHttpMessageConverter.class));
	}

	@Test
	void returnsJsonForUnknownTokenIntrospection() {
		contextRunner.run(context -> {
			assertThat(context).hasNotFailed();
			var request = new MockHttpServletRequest("POST", "/oauth2/token-info");
			request.setServletPath("/oauth2/token-info");
			request.setContentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
			request.addParameter("token", "unknown-token");
			request.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder()
					.encodeToString("test-client:test-secret".getBytes(StandardCharsets.UTF_8)));
			var response = new MockHttpServletResponse();

			context.getBean(FilterChainProxy.class).doFilter(request, response, new MockFilterChain());

			assertThat(response.getStatus()).isEqualTo(200);
			assertThat(MediaType.parseMediaType(response.getContentType()).isCompatibleWith(MediaType.APPLICATION_JSON))
					.isTrue();
			var json = context.getBean(JsonMapper.class).readTree(response.getContentAsString());
			assertThat(json.get("active").asBoolean()).isFalse();
		});
	}

	@Configuration(proxyBeanMethods = false)
	@EnableWebSecurity
	static class SecurityTestConfiguration {
	}
}
