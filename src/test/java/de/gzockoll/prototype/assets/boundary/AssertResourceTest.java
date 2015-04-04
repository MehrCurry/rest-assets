package de.gzockoll.prototype.assets.boundary;

import com.google.common.io.ByteStreams;
import com.mongodb.gridfs.GridFSDBFile;
import de.gzockoll.prototype.assets.AssetRepositoryApplication;
import de.gzockoll.prototype.assets.entity.Asset;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AssetRepositoryApplication.class)
@Slf4j
public class AssertResourceTest {
    @Autowired
    private AssetResource resource;

    @Autowired
    private GridFsTemplate template;

    @Test
    public void testSave() throws Exception {
        String data="bla";
        Asset asset=new Asset(data.getBytes(),"bla.txt");
        resource.save(asset);


        List<GridFSDBFile> files = template.find(null);
        assertThat(files).hasSize(1);
        files.forEach(f -> {
            try {
                System.out.println(f.toString());
                assertThat(ByteStreams.toByteArray(f.getInputStream())).isEqualTo(data.getBytes());
                assertThat(f.getMD5()).isEqualToIgnoringCase(asset.checksum());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Test
    public void testFindByMD5() throws IOException {
        String data="sdfsakjdfhkhsdkfhsdkjhfksdhfk";
        Asset asset=new Asset(data.getBytes(),"bla.txt");
        resource.save(asset);

        GridFSDBFile found = template.findOne(query(where("md5").is(asset.checksum().toLowerCase())));
        assertThat(found).isNotNull();
        assertThat(new String(ByteStreams.toByteArray(found.getInputStream()))).isEqualTo(data);
    }
    @Before
    @After
    public void tearDown() {
        template.delete(null);
    }
}