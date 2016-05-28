package de.gzockoll.prototype.ams.control;

import com.hazelcast.core.IMap;
import de.gzockoll.prototype.ams.entity.Asset;
import de.gzockoll.prototype.ams.pojo.Token;
import de.gzockoll.prototype.ams.pojo.TokenType;
import de.gzockoll.prototype.ams.repository.AssetRepository;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.*;

@Service public class TokenController {

    @Resource(name = "tokens")
    @Setter(AccessLevel.PACKAGE)
    private IMap<String, Token> tokenMap;

    @Autowired
    @Setter(AccessLevel.PACKAGE)
    private AssetRepository repository;

    public Token createToken(String payload, String type) {
        return createToken(payload,type,Optional.empty());
    }

    public Token createToken(String payload, String type, long ttl) {
        checkArgument(ttl > 0);
        return createToken(payload, type, Optional.of(ttl));
    }

    public Token createToken(String payload, String type, Optional<Long> ttl) {
        checkArgument(TokenType.valueOf(type.toUpperCase()) != null);

        List<Asset> assets = repository.findByMediaId(payload);
        Asset asset=getUniqueResultFromList(assets);

        TokenType tokenType = TokenType.valueOf(type.toUpperCase());
        return createToken(asset, tokenType, ttl);
    }

    private Asset getUniqueResultFromList(List<Asset> assets) {
        if (assets.isEmpty())
            throw new NoSuchElementException("No media found");
        checkState(assets.size() == 1, "More than one result found");
        return assets.get(0);
    }

    private Token createToken(Asset asset, TokenType tokenType, Optional<Long> ttl) {
        Token token = new Token(asset, tokenType);
        if (ttl.isPresent()) {
            tokenMap.put(token.getId(), token,ttl.get(), TimeUnit.SECONDS);
        } else {
            tokenMap.put(token.getId(), token);
        }
        return token;
    }

    public Optional<Token> getTokenFor(String id) {
        return Optional.ofNullable(tokenMap.get(id));
    }

    public Optional<Object> resolve(@NotNull String id,@NotNull TokenType tokenType) {
        checkNotNull(id);
        checkNotNull(tokenType);

        Optional<Object> opt = getTokenFor(id).filter(t -> t.getTokenType() == tokenType).map(Token::getPayload);
        if (opt.isPresent())
            tokenMap.remove(id);
        return opt;
    }

    public Collection<Token> findAll() {
        return tokenMap.values();
    }

    public Optional<Token> getTokenFor(String id, TokenType type) {
        return Optional.ofNullable(tokenMap.get(id)).filter(t -> t.getTokenType() == type);
    }

    public Token createToken(String namespace, String key, String type, Optional<Long> ttl) {
        checkArgument(TokenType.valueOf(type.toUpperCase()) != null);

        List<Asset> assets = repository.findByNameSpaceAndKey(namespace, key);
        Asset asset=getUniqueResultFromList(assets);

        TokenType tokenType = TokenType.valueOf(type.toUpperCase());
        return createToken(asset, tokenType, ttl);
    }
}
