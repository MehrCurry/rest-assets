package com.vjoon.se.core.camel;

import com.vjoon.se.core.control.Command;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CommandsRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:commands").routeId("commands")
                .split().body()
                .to("seda:command");

        from("seda:command").routeId("command")
                .process(ex -> {
                    ex.getIn().getBody(Command.class).run();
                });
    }
}
