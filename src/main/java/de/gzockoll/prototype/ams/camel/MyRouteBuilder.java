package de.gzockoll.prototype.ams.camel;

import de.gzockoll.prototype.ams.control.AssetController;
import de.gzockoll.prototype.ams.services.MediaService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyRouteBuilder extends RouteBuilder {
    @Value("${server.port}")
    private int port;

    @Value("${upload.root}")
    private String uploadRoot;

    @Value("${production.root}")
    private String productionRoot;

    @Value("${mirror.root}")
    private String mirrorRoot;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private ChecksumVerifier verifier;

    @Autowired
    private AssetController assetController;

    @Override
    public void configure() throws Exception {
        getContext().setTracing(true);
        errorHandler(deadLetterChannel("direct:failed").useOriginalMessage().maximumRedeliveries(3).redeliveryDelay(5000));

        from("file:"+ uploadRoot + "?delete=true&readLock=changed").routeId("Upload File")
                .threads(5).maxPoolSize(10)
                .setHeader("namespace", constant("imported"))
                .setHeader("key", simple("${header.CamelFileName}"))
                .bean("multipartCreator")
                .setHeader(Exchange.CONTENT_TYPE, constant("multipart/form-data"))
                .to("http4://localhost:" + port + "/assets")
                .to("log:failed?showAll=true&multiline=true");

        from("direct:production").routeId("production")
                .to("file:" + productionRoot + "?autoCreate=true");

        from("direct:saveToFile").routeId("saveToFile")
                .bean("fileWriter");

        from("direct:failed").routeId("failed")
            .errorHandler(defaultErrorHandler())
                .to("log:failed?showAll=true&multiline=true")
                .to("file:assets/failed?autoCreate=true");

        from("timer:dump?period=300000").routeId("dump")
                .errorHandler(defaultErrorHandler())
                .bean(mediaService, "getAll").setHeader("CamelFileName",constant("info.json")).marshal().json(
                JsonLibrary.Jackson).to("file:assets");

    }
}
