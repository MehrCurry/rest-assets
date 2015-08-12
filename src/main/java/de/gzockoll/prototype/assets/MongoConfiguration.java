package de.gzockoll.prototype.assets;

import de.gzockoll.prototype.assets.entity.Token;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MongoConfiguration implements InitializingBean {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        mongoTemplate.indexOps("files").ensureIndex(new Index().on("md5", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps("files").ensureIndex(new Index().on("filename", Sort.Direction.ASC));
        mongoTemplate.indexOps(Token.class).ensureIndex(new Index().on("createdAt", Sort.Direction.ASC).expire(60, TimeUnit.SECONDS));
    }

}
