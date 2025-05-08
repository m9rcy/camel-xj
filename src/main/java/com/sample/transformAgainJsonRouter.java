package com.sample;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class transformAgainJsonRouter extends RouteBuilder  {
    @Override
    public void configure() throws Exception {
        from("timer:hello?period={{timer.period}}").routeId("hello-2").to("direct:transform-3");
        from("direct:transform-3")
                .setHeader("creationDateTime", simple("${date:now:yyyy-MM-dd'T'HH:mm:ss}"))
                .setHeader("key.name", constant("1113333"))
                .setBody().simple("resource:classpath:new-input2.json")
                .convertBodyTo(String.class)
                .log("Before transform: ${body}")
                //.to("xj:identity?transformDirection=JSON2XML")
                .to("xj:classpath:new-2-transform.xsl?transformDirection=JSON2XML")
                .log("Transformed XML: ${body}");
                //.to("xslt-saxon:xason-transform.xsl")
                //.log("Transformed XML: ${body}");
    }
}
