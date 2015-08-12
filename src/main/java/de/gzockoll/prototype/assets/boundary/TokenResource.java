package de.gzockoll.prototype.assets.boundary;

import de.gzockoll.prototype.assets.entity.AssetDao;
import de.gzockoll.prototype.assets.entity.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tokens")
@Slf4j
public class TokenResource {
    @Autowired
    private AssetDao assetDao;

    @Autowired
    private MongoRepository<Token,String> repository;

    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<String> createTokenFor(@RequestParam(value = "key", required = true) String id) {
        if (assetDao.exists(id)) {
            Token token = Token.createFor(id);
            repository.save(token);
            return new ResponseEntity<>("http://localhost:9091/assets?token=" + token.getId(), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<List<String>> findAll() {
        List<Token> results = repository.findAll();
        return new ResponseEntity<>(results.stream().map(f -> f.toString()).collect(Collectors.toList()), HttpStatus.OK);
    }
}
