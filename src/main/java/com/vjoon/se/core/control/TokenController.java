package com.vjoon.se.core.control;

import com.vjoon.se.core.entity.Asset;
import com.vjoon.se.core.pojo.Token;
import com.vjoon.se.core.pojo.TokenType;
import com.vjoon.se.core.repository.AssetRepository;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;

import static com.google.common.base.Preconditions.*;

@Service public class TokenController {

    @Resource(name = "tokens")
    @Setter(AccessLevel.PACKAGE)
    private Map<String, Token> tokenMap;

    @Autowired
    @Setter(AccessLevel.PACKAGE)
    private AssetRepository repository;

    public Token createToken(String payload, String type) {
        checkArgument(TokenType.valueOf(type)!=null);

        List<Asset> mediaList = repository.findByMediaId(payload);
        if (mediaList.isEmpty())
            throw new NoSuchElementException("No media found:" + payload);
        checkState(mediaList.size() == 1, "mediaId not unique:" + payload);

        Token token = new Token(mediaList.get(0), TokenType.valueOf(type.toUpperCase()));
        tokenMap.put(token.getId(), token);
        return token;
    }

    public Optional<Token> getTokenFor(String id) {
        return Optional.ofNullable((Token) tokenMap.get(id));
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
        return Optional.ofNullable(tokenMap.get(id)).filter(t -> t.getTokenType()==type);
    }
}
