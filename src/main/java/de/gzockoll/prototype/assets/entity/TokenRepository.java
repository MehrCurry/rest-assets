package de.gzockoll.prototype.assets.entity;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by gzockoll on 12/08/15.
 */
public interface TokenRepository extends MongoRepository<Token,String> {
}
