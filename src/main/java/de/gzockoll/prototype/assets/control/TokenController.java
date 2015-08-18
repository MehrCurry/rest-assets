package de.gzockoll.prototype.assets.control;

import com.hazelcast.core.IMap;
import de.gzockoll.prototype.assets.pojo.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TokenController {
    @Resource(name = "tokens")
    private IMap<String,Token> tokenMap;

    public Token createToken(String payload) {
        Token token = new Token(payload);
        tokenMap.put(token.getId(),token);
        return token;
    }

    public Optional<Token> getTokenFor(String id) {
        return Optional.ofNullable((Token)tokenMap.get(id));
    }

    public Optional<String> resolve(String id) {
        return getTokenFor(id).map(Token::getPayload);
    }
}
