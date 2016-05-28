package de.gzockoll.prototype.ams.camel;

import de.gzockoll.prototype.ams.control.AssetController;
import de.gzockoll.prototype.ams.services.S3FileStore;
import de.gzockoll.prototype.ams.util.MD5Helper;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("s3Mirror")
public class S3RouteBuilder extends RouteBuilder {
    @Value("${server.port}")
    private int port;

    @Value("${s3Upload.root}")
    private String s3UploadRoot;

    @Autowired
    private ChecksumVerifier verifier;

    @Autowired
    private AssetController assetController;

    @Override
    public void configure() throws Exception {
        getContext().setTracing(false);
        errorHandler(deadLetterChannel("direct:failed").maximumRedeliveries(3));

        from("direct:" + S3FileStore.S3_QUEUE).routeId(S3FileStore.S3_QUEUE)
                .to("file:" + s3UploadRoot + "?flatten=true").bean(verifier);

        from("file:" + s3UploadRoot + "?recursive=true&delete=true&readLock=changed").routeId("toS3")
                .setHeader(S3Constants.CONTENT_MD5, method(new MD5Helper(), "calculateS3Hash"))
                .setHeader(S3Constants.KEY, simple("${file:name}"))
                .setHeader(S3Constants.CONTENT_LENGTH, simple("${file:size}"))
                .threads(2).maxPoolSize(4).maxQueueSize(10000)
                .filter().method(assetController, "assetExists(${file:name})")
                .to("aws-s3://gzbundles?accessKey=RAW(AKIAJYCTHK5TTAZOJX3A)&secretKey=RAW(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)&multiPartUpload=true&storageClass=REDUCED_REDUNDANCY&region=eu-central-1");
    }
}
