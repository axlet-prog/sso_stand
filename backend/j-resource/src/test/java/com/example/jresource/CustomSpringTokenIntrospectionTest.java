package com.example.jresource;

import com.example.jresource.dto.CustomOAuth2AuthenticatedPrincipal;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomSpringTokenIntrospectionTest {

	private HttpServer server;
	private CustomSpringTokenIntrospection introspector;
	private final AtomicReference<String> response = new AtomicReference<>("{\"active\":false}");
	private final AtomicInteger status = new AtomicInteger(200);
	private final AtomicReference<String> requestMethod = new AtomicReference<>();
	private final AtomicReference<String> requestContentType = new AtomicReference<>();
	private final AtomicReference<String> requestAuthorization = new AtomicReference<>();
	private final AtomicReference<String> requestBody = new AtomicReference<>();

	@BeforeEach
	void startIntrospectionEndpoint() throws IOException {
		server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/oauth2/token-info", exchange -> {
			requestMethod.set(exchange.getRequestMethod());
			requestContentType.set(exchange.getRequestHeaders().getFirst("Content-Type"));
			requestAuthorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
			requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
			byte[] body = response.get().getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().set("Content-Type", "application/json");
			exchange.sendResponseHeaders(status.get(), body.length == 0 ? -1 : body.length);
			exchange.getResponseBody().write(body);
			exchange.close();
		});
		server.start();
		introspector = new CustomSpringTokenIntrospection(
				"http://127.0.0.1:" + server.getAddress().getPort() + "/oauth2/token-info",
				"resource-client", "resource-secret", new JacksonJsonHttpMessageConverter()
		);
	}

	@AfterEach
	void stopIntrospectionEndpoint() {
		if (server != null) {
			server.stop(0);
		}
	}

	@Test
	void sendsFormAndClientAuthenticationAndReadsClientCredentialsToken() {
		response.set("""
				{"active":true,"clientId":"api-client","scopes":["read.scope"],
				 "iat":1750000000,"exp":1750003600,"tokenType":"Bearer"}
				""");

		var principal = (CustomOAuth2AuthenticatedPrincipal) introspector.introspect("opaque+token&value");

		assertThat(requestMethod.get()).isEqualTo("POST");
		assertThat(requestContentType.get()).startsWith("application/x-www-form-urlencoded");
		assertThat(requestBody.get()).isEqualTo("token=opaque%2Btoken%26value");
		assertThat(requestAuthorization.get()).isEqualTo("Basic " + Base64.getEncoder()
				.encodeToString("resource-client:resource-secret".getBytes(StandardCharsets.UTF_8)));
		assertThat(principal.getName()).isEqualTo("api-client");
		assertThat(principal.getAuthorities()).isEmpty();
		assertThat(principal.getIssuedAt()).isEqualTo(Instant.ofEpochSecond(1750000000));
		assertThat(principal.getExpiresAt()).isEqualTo(Instant.ofEpochSecond(1750003600));
		assertThat(principal.getScopes()).containsExactly("read.scope");
	}

	@Test
	void restoresUserAndAuthoritiesFromSsoResponse() {
		response.set("""
				{"active":true,"clientId":"api-client","principal":{
				 "id":"f77b82c3-35c3-45eb-a93e-70ad8daa9011","username":"alice",
				 "birthday":"2000-01-02","authorities":["GET_OWN_DATA"]}}
				""");

		var principal = introspector.introspect("user-token");

		assertThat(principal.getName()).isEqualTo("alice");
		assertThat(principal.getAuthorities()).extracting("authority").containsExactly("GET_OWN_DATA");
	}

	@Test
	void rejectsInactiveToken() {
		assertThatThrownBy(() -> introspector.introspect("inactive-token"))
				.isInstanceOf(BadOpaqueTokenException.class);
	}

	@Test
	void rejectsResponseWithoutActiveFlag() {
		response.set("{}");

		assertThatThrownBy(() -> introspector.introspect("token"))
				.isInstanceOf(BadOpaqueTokenException.class);
	}

	@Test
	void reportsEmptyResponseAsIntrospectionFailure() {
		response.set("");

		assertThatThrownBy(() -> introspector.introspect("token"))
				.isInstanceOf(OAuth2IntrospectionException.class)
				.hasMessageContaining("empty");
	}

	@Test
	void reportsRejectedClientAuthenticationAsIntrospectionFailure() {
		status.set(401);
		response.set("{\"error\":\"invalid_client\"}");

		assertThatThrownBy(() -> introspector.introspect("token"))
				.isInstanceOf(OAuth2IntrospectionException.class)
				.hasMessageContaining("401");
	}
}
