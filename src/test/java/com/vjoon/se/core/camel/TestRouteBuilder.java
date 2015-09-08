package com.vjoon.se.core.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TestRouteBuilder extends RouteBuilder {
    @Value("${test.root}")
    private String testRoot;


    @Override public void configure() throws Exception {
        from("direct:test").routeId("test")
                .to("file:" + testRoot + "?autoCreate=true")
                .to("log:bla?showAll=true&multiline=true");
    }
}
