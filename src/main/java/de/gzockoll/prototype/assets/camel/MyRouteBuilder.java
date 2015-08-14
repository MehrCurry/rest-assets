package de.gzockoll.prototype.assets.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Service;

@Service
public class MyRouteBuilder extends RouteBuilder {
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

        from("file:assets/inbox?delete=true&idempotent=true&readLock=rename&idempotentKey=${file:name}-${file:size}").to("direct:store");
        from("direct:store").multicast().to("direct:local", "direct:gridfs","direct:production","direct:s3");
        from("direct:local").to("file:assets/mirror");
        from("direct:production").to("file:assets/production");
        from("direct:gridfs").to("bean:assetResource?method=fileImport");
        from("direct:s3")
                .setHeader(S3Constants.KEY, simple("${file:name}"))
                .setHeader(S3Constants.CONTENT_LENGTH,simple("${file:size}"))
                .to("aws-s3://gzbundles?accessKey=RAW(AKIAJYCTHK5TTAZOJX3A)&secretKey=RAW(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)&delay=5000&maxMessagesPerPoll=5&region=eu-central-1");
        rest("/upload")
                .post().to("bean:assetResource?method=fileImport");
    }
}
