package com.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.jackson.JsonFormat;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cloudevents.CloudEvent;
import org.apache.camel.component.cloudevents.CloudEvents;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;

//@Component
public class KafkaRoute extends RouteBuilder {
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(JsonFormat.getCloudEventJacksonModule());
    @Override
    public void configure() throws Exception {



        from("timer:cloudProducer?period=5000")
                .process(exchange -> {
                    String[] fruits = {"Apples", "Oranges", "Bananas", "Mangoes"};
                    String[] rarities = {"Common", "Uncommon", "Rare", "Legendary"};
                    List<String> itemTags = Arrays.asList("a", "b", "c", "d", "e");

                    Random rand = new Random();

                    String id = fruits[rand.nextInt(fruits.length)];
                    String rarity = rarities[rand.nextInt(rarities.length)];
                    Map<String, Object> others = new HashMap<>();
                    others.put("value", rand.nextDouble() * 10);

                    Collections.shuffle(itemTags);
                    List<String> items = itemTags.subList(0, 3);

                    FruitEvent event = new FruitEvent(id, rarity, others, items);

                    io.cloudevents.CloudEvent cloudEvent = CloudEventBuilder.v1()
                            .withId(UUID.randomUUID().toString())
                            .withType("com.example.fruitevent")
                            .withSource(URI.create("/camel/fruits"))
                            .withTime(OffsetDateTime.now())
                            .withData("application/json", objectMapper.writeValueAsBytes(event))
                            .build();

                    exchange.getMessage().setBody(cloudEvent);

                    // Set CloudEvent headers
//                    exchange.getIn().setHeader("ce-id", UUID.randomUUID().toString());
//                    exchange.getIn().setHeader("ce-type", "com.example.fruitevent");
//                    exchange.getIn().setHeader("ce-source", "/camel/fruits");
//                    exchange.getIn().setHeader("ce-specversion", "1.0");
//                    exchange.getIn().setHeader("ce-time", OffsetDateTime.now().toString());
                })
                .to("kafka:cloudevents-demo?"
                        + "brokers=localhost:9092"
                        + "&valueSerializer=io.cloudevents.kafka.CloudEventSerializer"
                        + "&keySerializer=org.apache.kafka.common.serialization.StringSerializer");

        from("kafka:cloudevents-demo?"
                + "brokers=localhost:9092"
                + "&groupId=my-group"
                + "&valueDeserializer=io.cloudevents.kafka.CloudEventDeserializer"
                + "&keyDeserializer=org.apache.kafka.common.serialization.StringDeserializer")
                .process(exchange -> {
                    CloudEventV1 event = exchange.getIn().getBody(CloudEventV1.class);
                    System.out.println("🔥 Received FruitEvent: " + event);
                });
//            .process(exchange -> {
//            CloudEvent event = exchange.getIn().getBody(CloudEvent.class);
//            // If needed: deserialize to your event class
//            byte[] data = event.getData() != null ? event.getData().toBytes() : null;
//            if (data != null) {
//                FruitEvent fruit = CloudEventHelper.objectMapper.readValue(data, FruitEvent.class);
//                System.out.println("🎯 Received: " + fruit);
//            }
//        });

    }
}
