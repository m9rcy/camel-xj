package com.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.camel.Expression;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//@Component
public class NewTransformRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:hello?period={{timer.period}}").routeId("hello-1").to("direct:transformOrder");
        from("direct:transformOrder")
                .process(exchange -> {
                    Map<String, List<String>> actionMap = Map.of("added", List.of("OW111", "OW222"), "delete", List.of("OW444"));
                    exchange.getMessage().setHeader("ordernum", "OR100");
                    exchange.getMessage().setHeader("addedList", List.of("OW111", "OW222"));
                    exchange.getMessage().setHeader("deletedList", List.of("OW444"));

                    // Convert Map into wrapper object
                    ActionWrapper wrapper = new ActionWrapper();
                    for (Map.Entry<String, List<String>> entry : actionMap.entrySet()) {
                        ActionWrapper.Action action = new ActionWrapper.Action();
                        action.name = entry.getKey();
                        action.values = entry.getValue(); // will be empty list if none
                        wrapper.actions.add(action);
                    }

                    XmlMapper mapper = new XmlMapper();
                    String xml = mapper.writeValueAsString(wrapper);
                    System.out.println(xml);
                    exchange.getMessage().setBody(xml);
                })
                //.setBody(constant("{\"added\": [\"OW100\", \"OW200\"], \"deleted\": [\"OW202\", \"OW201\"]}"))
                .to("xslt:classpath:xslt-transform-new.xsl")
                .log("Transformed XML: ${body}");
    }
}
