package com.sample;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.builder.Namespaces;
import org.springframework.stereotype.Component;

@Component
public class XpathRouter extends RouteBuilder {

    Namespaces namespaces = new Namespaces("ns", "http://example.com");
    @Override
    public void configure() throws Exception {
        from("file:src/main/resources?fileName=sample-input4.xml&noop=true")
                .routeId("test-file-to-direct")
                .log("Loaded test XML file")
                .to("direct:start");

        from("direct:start")
                .choice()
                .when(xpath("/ns:QueryResponse[@rsCount > 0 or @rsTotal > 0] and ns:TP_OB_SET/ns:TPBLOCK[ns:STATUS='COMMISSION']", namespaces))
                .log("Response is valid and has records")
                .setHeader("parentId", constant("AEST_1_NEW"))
                .to("xslt:classpath:transform-commission.xsl")
                .log("Transformed XML: ${body}")
                .otherwise()
                .log("No records found or invalid response");
    }
}
