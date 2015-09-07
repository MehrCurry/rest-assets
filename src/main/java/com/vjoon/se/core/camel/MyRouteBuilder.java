package com.vjoon.se.core.camel;

import com.vjoon.se.core.control.AssetController;
import com.vjoon.se.core.services.MediaService;
import com.vjoon.se.core.services.S3FileStore;
import com.vjoon.se.core.util.MD5Helper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyRouteBuilder extends RouteBuilder {
    @Value("${server.port}")
    private int port;
    @Autowired
    private MediaService mediaService;

    @Autowired
    private ChecksumVerifier verifier;

    @Autowired
    private AssetController assetController;

    @Override
    public void configure() throws Exception {
        getContext().setTracing(false);
        errorHandler(deadLetterChannel("direct:failed").maximumRedeliveries(3));

        from("file:assets/upload?delete=true&readLock=changed").routeId("Upload File")
                .threads(5).maxPoolSize(10)
                .setHeader("namespace", constant("imported"))
                .setHeader("key", simple("${header.CamelFileName}"))
                .beanRef("multipartCreator")
                .setHeader(Exchange.CONTENT_TYPE, constant("multipart/form-data"))
                .to("http4://localhost:" + port +"/assets")
                .to("log:failed?showAll=true&multiline=true");
        ;

        from("direct:production").routeId("production")
                .to("file:assets?autoCreate=true");

        from("direct:mirror").routeId("mirror")
                .to("file:assets?autoCreate=true")
                .bean(verifier);

        from("direct:" + S3FileStore.S3_QUEUE).routeId(S3FileStore.S3_QUEUE)
                .to("file:assets/" + S3FileStore.S3_QUEUE + "?flatten=true").bean(verifier);

        from("file:assets/" + S3FileStore.S3_QUEUE + "?recursive=true&delete=true&readLock=changed").routeId("toS3")
                .setHeader(S3Constants.CONTENT_MD5, method(new MD5Helper(), "calculateS3Hash"))
                .setHeader(S3Constants.KEY, simple("${file:name}"))
                .setHeader(S3Constants.CONTENT_LENGTH, simple("${file:size}"))
                .threads(2).maxPoolSize(4).maxQueueSize(10000)
                .filter().method(assetController, "assetExists(${file:name})")
                .to("aws-s3://gzbundles?accessKey=RAW(AKIAJYCTHK5TTAZOJX3A)&secretKey=RAW(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)&multiPartUpload=true&storageClass=REDUCED_REDUNDANCY&region=eu-central-1");

        from("direct:failed").routeId("failed")
            .errorHandler(defaultErrorHandler())
                .to("log:failed?showAll=true&multiline=true");
            // .to("file:assets/failed?autoCreate=true");

        from("timer:dump?period=300000").routeId("dump")
                .errorHandler(defaultErrorHandler())
                .bean(mediaService, "getAll").setHeader("CamelFileName",constant("info.json")).marshal().json(
                JsonLibrary.Jackson).to("file:assets");

    }
}
