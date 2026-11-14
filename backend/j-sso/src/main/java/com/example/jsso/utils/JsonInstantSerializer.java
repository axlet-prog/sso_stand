package com.example.jsso.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.Instant;

public class JsonInstantSerializer extends JsonSerializer<Instant> {

    @Override
    public void serialize(Instant instant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
        throws IOException {
        if (instant != null) {
            jsonGenerator.writeNumber(instant.getEpochSecond());
        } else {
            jsonGenerator.writeNull();
        }
    }
}
