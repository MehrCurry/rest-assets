package de.gzockoll.prototype.assets.camel;

import de.gzockoll.prototype.assets.services.MediaService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyRouteBuilder extends RouteBuilder {
    @Autowired
    private MediaService mediaService;

    @Override
    public void configure() throws Exception {
        getContext().setTracing(true);
        errorHandler(deadLetterChannel("file:asset/failed")
                .maximumRedeliveries(3).redeliveryDelay(5000));

        // configure we want to use servlet as the component for the rest DSL
        // and we enable json binding mode
        restConfiguration().component("servlet").bindingMode(RestBindingMode.json);
                        // setup context path and port number that Apache Tomcat will deploy
                        // this application with, as we use the servlet component, then we
                        // need to aid Camel to tell it these details so Camel knows the url
                        // to the REST services.
                        // Notice: This is optional, but needed if the RestRegistry should
                        // enlist accurate information. You can access the RestRegistry
                        // from JMX at runtime
                // .contextPath("camel-example-servlet-rest-tomcat/rest").port(8080);

        from("file:assets/inbox?delete=true&idempotent=true&readLock=rename&idempotentKey=${file:name}-${file:size}").routeId("Import File").to("direct:store");
        from("direct:store").routeId("Send out")
                .setHeader("media").method(mediaService, "createMediaInfo")
                .setHeader("CamelFileName").simple("${header.media.filename}")
                .multicast().to("direct:local", "direct:gridfs", "direct:production", "direct:s3");
        from("direct:local").routeId("mirror")
                .to("file:assets/mirror")
                .process(new ChecksumVerifier());
        from("direct:production").routeId("production").to("file:assets/production");
        from("direct:gridfs").routeId("gridFS").to("bean:assetResource?method=fileImport").bean(mediaService, "finishGridFsTransfer");
        ;
        from("direct:s3").routeId("s3tmp")
                .setHeader("CamelFileName",simple("${header.media.mediaId}"))
                .to("file:assets/s3tmp");
        from("file:assets/s3tmp?delete=true&readLock=rename").routeId("toS3")
                .setHeader("media").method(mediaService,"findMedidInfo")
                //.setHeader(S3Constants.CONTENT_MD5,simple("${header.media.hash}"))
                .setHeader(S3Constants.KEY, simple("${file:name}"))
                .setHeader(S3Constants.CONTENT_LENGTH, simple("${file:size}"))
                .to("aws-s3://gzbundles?accessKey=RAW(AKIAJYCTHK5TTAZOJX3A)&secretKey=RAW(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)&delay=5000&maxMessagesPerPoll=5&region=eu-central-1")
                .bean(mediaService,"finishS3Transfer");
        rest("/upload")
                .post().to("bean:assetResource?method=fileImport");
        /*
        from("timer:dump?period=30000").bean(mediaService,"getAll")
                .setHeader("CamelFileName",constant("info.json"))
                .marshal().json().to("file:assets");  */
    }
}
