package com.example.jresource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = "logging.level.root=WARN")
class ResourceServerApplicationTest {

	@Value("${local.server.port}")
	private int port;

	private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
	private final JsonMapper jsonMapper = JsonMapper.builder().build();

	@Test
	void healthcheckIsPublicAndDoesNotCreateSession() throws Exception {
		var response = get("/actuator/health");

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(jsonMapper.readTree(response.body()).get("status").asString()).isEqualTo("UP");
		assertThat(response.headers().allValues("Set-Cookie")).isEmpty();
	}

	@Test
	void protectedApiRequiresBearerTokenAndDoesNotCreateSession() throws Exception {
		var response = get("/test");

		assertThat(response.statusCode()).isEqualTo(401);
		assertThat(response.headers().firstValue("WWW-Authenticate").orElseThrow()).startsWith("Bearer");
		assertThat(response.headers().allValues("Set-Cookie")).isEmpty();
	}

	@Test
	void swaggerCanReadApiDefinitionWithPublicNginxOAuthUrls() throws Exception {
		var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/v3/api-docs"))
				.header("Origin", "http://localhost:7778").timeout(Duration.ofSeconds(10)).GET().build();

		var response = client.send(request, HttpResponse.BodyHandlers.ofString());
		var document = jsonMapper.readTree(response.body());

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.headers().firstValue("Access-Control-Allow-Origin"))
				.contains("http://localhost:7778");
		assertThat(document.get("paths").has("/test")).isTrue();
		assertThat(document.get("paths").has("/test-auth")).isTrue();
		assertThat(document.at("/components/securitySchemes/Authorization code flow/flows/authorizationCode/tokenUrl")
				.asString()).isEqualTo("http://localhost:8080/oauth2/token");
	}

	private HttpResponse<String> get(String path) throws Exception {
		var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
				.timeout(Duration.ofSeconds(10)).GET().build();
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}
}
