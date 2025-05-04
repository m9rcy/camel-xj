package com.sample;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * A simple Camel route that triggers from a timer and calls a bean and prints to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
//@Component
public class MySpringBootRouter extends RouteBuilder {

    @Override
    public void configure() {
        from("timer:hello?period={{timer.period}}").routeId("hello")
            .transform().method("myBean", "saySomething")
            .filter(simple("${body} contains 'foo'"))
                .to("log:foo")
            .end()
            .to("stream:out").to("direct:transform-2");

        from("direct:transform-1")
                .setHeader("creationDateTime", simple("${date:now:yyyy-MM-dd'T'HH:mm:ss}"))
                .setBody().simple("resource:classpath:input.json")
                .convertBodyTo(String.class)
                .log("Before transform: ${body}")
                .to("xj:classpath:xj-transform.xsl?transformDirection=JSON2XML")
                .log("Transformed XML: ${body}");

        from("direct:transform-2")
                .setHeader("creationDateTime", simple("${date:now:yyyy-MM-dd'T'HH:mm:ss}"))
                .setBody().simple("resource:classpath:input.json")
                .convertBodyTo(String.class)
                .log("Before transform: ${body}")
                .to("xj:identity?transformDirection=JSON2XML")
                .log("After transform: ${body}")
                .to("xslt-saxon:xason-transform.xsl")
                .log("Transformed XML: ${body}");
    }

}


