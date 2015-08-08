package de.gzockoll.prototype.assets.entity;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class AssetDao {

    @Autowired
    private GridFsTemplate template;

    public GridFSFile save(Asset a) {
        return template.store(a.getAsStream(),a.getFilename(),a.getMimeType());
    }

    public Optional<GridFSDBFile> findById(String param) {
        return findByKeyValue("_id", param);
    }

    public Optional<GridFSDBFile> findByHash(String param) {
        return findByKeyValue("md5",param);
    }

    public Optional<GridFSDBFile> findByFilename(String param) {
        return findByKeyValue("filename",param);
    }

    public Optional<GridFSDBFile> findByKeyValue(String key,String value) {
        return Optional.ofNullable(template.findOne(query(where(key).is(value))));
    }

    public void deleteAll() {
        template.delete(null);
    }

    public void deleteByHash(String param) {
         template.delete(query(where("md5").is(param)));
    }

    public void deleteByKeyValue(String key, String value) {
        template.delete(query(where(key).is(value)));
    }

    public List<GridFSDBFile> findAll() { return template.find(null);
    }
}
