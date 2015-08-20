package de.gzockoll.prototype.assets.camel;

import de.gzockoll.prototype.assets.entity.VaultType;
import de.gzockoll.prototype.assets.services.MediaService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class MyRouteBuilder extends RouteBuilder {
    @Autowired
    private MediaService mediaService;

    @Override
    public void configure() throws Exception {
        getContext().setTracing(true);
        errorHandler(deadLetterChannel("direct:failed")
                .maximumRedeliveries(3).redeliveryDelay(2000));

        from("file:assets/upload?delete=true&readLock=rename").routeId("Upload File")
                .beanRef("multipartCreator")
                .log("POST ${header.CamelFileName} to /upload")
                .setHeader(Exchange.CONTENT_TYPE, constant("multipart/form-data"))
                .to("http4://localhost:9091/media")
                .routeId("HTTP response status: ${header.CamelHttpResponseCode}")
                .log("HTTP response body:\n${body}");

        from("file:assets/inbox?delete=true&idempotent=true&readLock=rename&idempotentKey=${file:name}-${file:size}").routeId("Import File").to("direct:store");
        from("direct:production").routeId("production")
                .setHeader("target", constant(VaultType.PRODUCTION))
                .to("file:assets/production")
                .process(new ChecksumVerifier())
                .bean(mediaService, "finished");

        from("direct:failed").routeId("failed")
                .to("log:bla?showAll=true&multiline=true");
        from("timer:dump?period=300000").routeId("dump")
                .bean(mediaService, "getAll")
                .setHeader("CamelFileName",constant("info.json"))
                .marshal().json().to("file:assets");

    }
}
