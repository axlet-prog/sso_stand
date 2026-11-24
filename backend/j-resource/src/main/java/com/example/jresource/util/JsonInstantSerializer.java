package com.example.jresource.util;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.time.Instant;

public class JsonInstantSerializer extends ValueSerializer<Instant> {

	@Override
	public void serialize(Instant instant, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
		if (instant != null) {
			jsonGenerator.writeNumber(instant.getEpochSecond());
		} else {
			jsonGenerator.writeNull();
		}
	}
}
