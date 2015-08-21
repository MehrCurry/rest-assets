package de.gzockoll.prototype.assets.camel;

import de.gzockoll.prototype.assets.services.MediaService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyRouteBuilder extends RouteBuilder {
    @Autowired
    private MediaService mediaService;

    @Override
    public void configure() throws Exception {
        getContext().setTracing(true);

        from("file:assets/upload?delete=true&readLock=changed").routeId("Upload File")
                .setHeader("namespace", constant("imported"))
                .beanRef("multipartCreator")
                .log("POST ${header.CamelFileName} to /upload")
                .setHeader(Exchange.CONTENT_TYPE, constant("multipart/form-data"))
                .to("http4://localhost:9091/assets")
                .routeId("HTTP response status: ${header.CamelHttpResponseCode}")
                .log("HTTP response body:\n${body}");

        from("direct:failed").routeId("failed")
                .to("log:bla?showAll=true&multiline=true");
        from("timer:dump?period=300000").routeId("dump")
                .bean(mediaService, "getAll")
                .setHeader("CamelFileName",constant("info.json"))
                .marshal().json().to("file:assets");

    }
}
