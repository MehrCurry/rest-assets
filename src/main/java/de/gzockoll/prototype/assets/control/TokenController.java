package de.gzockoll.prototype.assets.control;

import com.hazelcast.core.IMap;
import de.gzockoll.prototype.assets.pojo.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TokenController {
    // @Autowired
    // private IMap<String,Token> tokenMap;
    private Map<String,Token> tokenMap=new HashMap<>();
    public Token createToken(String payload) {
        Token token = new Token(payload);
        tokenMap.put(token.getId(),token);
        return token;
    }

    public Optional<Token> getTokenFor(String id) {
        return Optional.ofNullable((Token)tokenMap.get(id));
    }
}
