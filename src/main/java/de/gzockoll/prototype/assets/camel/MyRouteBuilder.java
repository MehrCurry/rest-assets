package de.gzockoll.prototype.assets.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Service;

@Service
public class MyRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        getContext().setTracing(true);
        from("file:assets?delete=true$idempotent=true&readLock=fileLock").to("bean:assetResource?method=fileImport");

    }
}
