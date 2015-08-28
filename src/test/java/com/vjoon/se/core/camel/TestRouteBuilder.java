package com.vjoon.se.core.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class TestRouteBuilder extends RouteBuilder {

    @Override public void configure() throws Exception {
        from("direct:test").routeId("test")
                .to("file:assets?autoCreate=true")
                .to("log:bla?showAll=true&multiline=true");
    }
}
