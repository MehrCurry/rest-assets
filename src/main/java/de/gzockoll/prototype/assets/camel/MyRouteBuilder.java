package de.gzockoll.prototype.assets.camel;

import de.gzockoll.prototype.assets.entity.VaultType;
import de.gzockoll.prototype.assets.services.MediaService;
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
        errorHandler(deadLetterChannel("direct:failed")
                .maximumRedeliveries(3).redeliveryDelay(2000));

        from("file:assets/inbox?delete=true&idempotent=true&readLock=rename&idempotentKey=${file:name}-${file:size}").routeId("Import File").to("direct:store");
        from("direct:store").routeId("Send out")
                .setHeader("media").method(mediaService, "createMediaInfo")
                .setHeader("CamelFileName").simple("${header.media.filename}")
                .multicast().to("direct:archive", "direct:production");
        from("direct:archive").routeId("archive")
                .setHeader("target", constant(VaultType.ARCHIVE))
                .to("file:assets/archive")
                .process(new ChecksumVerifier())
                .bean(mediaService, "finished");
        from("direct:production").routeId("production")
                .setHeader("target", constant(VaultType.PRODUCTION))
                .to("file:assets/production")
                .process(new ChecksumVerifier())
                .bean(mediaService, "finished");

        from("direct:gridfs").routeId("gridFS")
                .setHeader("target", constant(VaultType.GRIDFS))
                .to("bean:assetResource?method=fileImport")
                .bean(mediaService, "finished");
        from("direct:s3").routeId("s3tmp")
                .setHeader("CamelFileName", simple("${header.media.mediaId}"))
                .to("file:assets/s3tmp");
        from("file:assets/s3tmp?delete=true&readLock=rename").routeId("toS3")
                .setHeader("target", constant(VaultType.S3))
                .setHeader("media").method(mediaService,"findMediaInfo")
                //.setHeader(S3Constants.CONTENT_MD5,simple("${header.media.hash}"))
                .setHeader(S3Constants.KEY, simple("${file:name}"))
                .setHeader(S3Constants.CONTENT_LENGTH, simple("${file:size}"))
                .to("aws-s3://gzbundles?accessKey=RAW(AKIAJYCTHK5TTAZOJX3A)&secretKey=RAW(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)&delay=5000&maxMessagesPerPoll=5&region=eu-central-1")
                .bean(mediaService, "finished");

        from("direct:failed")
                .setHeader("CamelFileName",simple("${header.media.originalFilename}"))
                .to("file:assets/failed");
        from("timer:dump?period=30000").bean(mediaService,"getAll")
                .setHeader("CamelFileName",constant("info.json"))
                .marshal().json().to("file:assets");

    }
}
