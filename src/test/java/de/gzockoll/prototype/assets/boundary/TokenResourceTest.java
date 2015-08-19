package de.gzockoll.prototype.assets.boundary;

import com.google.common.collect.ImmutableList;
import com.hazelcast.core.IMap;
import de.gzockoll.prototype.assets.boundary.rest.TokenResource;
import de.gzockoll.prototype.assets.control.TokenController;
import de.gzockoll.prototype.assets.entity.Media;
import de.gzockoll.prototype.assets.pojo.Token;
import de.gzockoll.prototype.assets.repository.MediaRepository;
import org.junit.Test;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by guido on 19.08.15.
 */
public class TokenResourceTest {
    private TokenController tokenController=new TokenController();
    @Test
    public void testStream() throws IOException {
        Media media=new Media("test.txt");
        media.setContentType("plain/text");
        String mediaId=media.getMediaId();

        IMap<String,Token> imap=mock(IMap.class);
        tokenController.setTokenMap(imap);

        TokenResource resource=new TokenResource();
        resource.setController(tokenController);

        Token t=resource.createToken(media.getMediaId());
        when(imap.get(t.getId())).thenReturn(t);

        HttpEntity<InputStreamResource> result = resource.getDocument(t.getId());
        assertThat(result).isNotNull();

    }

}