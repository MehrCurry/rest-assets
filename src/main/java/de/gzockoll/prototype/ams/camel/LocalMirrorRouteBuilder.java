package de.gzockoll.prototype.ams.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("localMirror")
public class LocalMirrorRouteBuilder extends RouteBuilder {
    @Value("${server.port}")
    private int port;

    @Value("${mirror.root}")
    private String mirrorRoot;

    @Autowired
    private ChecksumVerifier verifier;

    @Override
    public void configure() throws Exception {
        getContext().setTracing(false);
        errorHandler(deadLetterChannel("direct:failed").maximumRedeliveries(3));


        from("direct:mirror").routeId("mirror")
                .to("file:" + mirrorRoot + "?autoCreate=true")
                .bean(verifier);
    }
}
