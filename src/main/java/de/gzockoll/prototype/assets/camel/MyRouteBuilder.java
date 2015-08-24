package de.gzockoll.prototype.assets.camel;

import de.gzockoll.prototype.assets.services.MediaService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyRouteBuilder extends RouteBuilder {
    @Autowired
    private MediaService mediaService;

    @Override
    public void configure() throws Exception {
        getContext().setTracing(true);
        errorHandler(loggingErrorHandler());

        from("file:assets/upload?delete=true&readLock=changed").routeId("Upload File")
                .errorHandler(deadLetterChannel("direct:failed").maximumRedeliveries(3))
                .setHeader("namespace", constant("imported"))
                .setHeader("key",simple("${header.CamelFileName}"))
                .beanRef("multipartCreator")
                .log("POST ${header.CamelFileName} to /upload")
                .setHeader(Exchange.CONTENT_TYPE, constant("multipart/form-data"))
                .to("http4://localhost:9091/assets")
                .routeId("HTTP response status: ${header.CamelHttpResponseCode}")
                .log("HTTP response body:\n${body}");

        from("direct:fileStore").routeId("fileStore")
                .errorHandler(deadLetterChannel("direct:failed").maximumRedeliveries(3))
                .to("file:assets?autoCreate=true")
                .to("log:bla?showAll=true&multiline=true");
        from("direct:s3Upload").routeId("toS3")
                .loadBalance().circuitBreaker(2,1000L,Exception.class)
                .to("aws-s3://gzbundles?accessKey=RAW(AKIAJYCTHK5TTAZOJX3A)&secretKey=RAW(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)&delay=5000&maxMessagesPerPoll=5&region=eu-central-1");

        from("direct:failed").routeId("failed")
                .to("log:bla?showAll=true&multiline=true");

        from("timer:dump?period=300000").routeId("dump")
                .bean(mediaService, "getAll")
                .setHeader("CamelFileName",constant("info.json"))
                .marshal().json().to("file:assets");

    }
}
