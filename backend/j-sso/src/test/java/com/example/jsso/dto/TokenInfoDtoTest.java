package com.example.jsso.dto;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenInfoDtoTest {

	private final JsonMapper mapper = JsonMapper.builder().build();

	@Test
	void serializesTokenTimestampsAsEpochSeconds() {
		Instant issuedAt = Instant.parse("2026-11-19T10:00:00.123Z");
		TokenInfoDto token = TokenInfoDto.builder()
				.active(true)
				.nbf(issuedAt)
				.iat(issuedAt)
				.exp(issuedAt.plusSeconds(600))
				.build();

		var json = mapper.readTree(mapper.writeValueAsString(token));

		assertTrue(json.get("nbf").isIntegralNumber());
		assertEquals(issuedAt.getEpochSecond(), json.get("nbf").asLong());
		assertEquals(issuedAt.getEpochSecond(), json.get("iat").asLong());
		assertEquals(issuedAt.plusSeconds(600).getEpochSecond(), json.get("exp").asLong());
	}

	@Test
	void serializesAbsentTokenTimestampsAsNull() {
		var json = mapper.readTree(mapper.writeValueAsString(TokenInfoDto.builder().active(false).build()));

		assertTrue(json.get("nbf").isNull());
		assertTrue(json.get("iat").isNull());
		assertTrue(json.get("exp").isNull());
	}
}
