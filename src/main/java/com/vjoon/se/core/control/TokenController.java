package com.vjoon.se.core.control;

import com.hazelcast.core.IMap;
import com.vjoon.se.core.entity.Media;
import com.vjoon.se.core.pojo.Token;
import com.vjoon.se.core.repository.MediaRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

@Service public class TokenController {

    @Resource(name = "tokens") @Setter private IMap<String, Token> tokenMap;

    @Autowired private MediaRepository repository;

    public Token createToken(String payload) {
        List<Media> mediaList = repository.findByMediaId(payload);
        if (mediaList.isEmpty())
            throw new NoSuchElementException("No media found:" + payload);
        checkState(mediaList.size() == 1, "mediaId not unique:" + payload);

        Token token = new Token(mediaList.get(0));
        tokenMap.put(token.getId(), token);
        return token;
    }

    public Optional<Token> getTokenFor(String id) {
        return Optional.ofNullable((Token) tokenMap.get(id));
    }

    public Optional<Object> resolve(String id) {

        Optional<Object> opt = getTokenFor(id).map(Token::getPayload);
        tokenMap.remove(id);
        return opt;
    }

    public Collection<Token> findAll() {
        return tokenMap.values();
    }
}
