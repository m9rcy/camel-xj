package com.sample;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;

public class CloudEventHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(JsonFormat.getCloudEventJacksonModule());

    public static CloudEvent createCloudEvent(FruitEvent data) throws Exception {
        // Serialize your data (FruitEvent) to JSON
        byte[] dataBytes = objectMapper.writeValueAsBytes(data);

        return CloudEventBuilder.v1()
            .withId(UUID.randomUUID().toString())
            .withType("com.example.fruitevent")
            .withSource(URI.create("/camel/fruits"))
            .withTime(OffsetDateTime.now())
            .withData(dataBytes).withDataContentType("application/json")
            .build();
    }
}
