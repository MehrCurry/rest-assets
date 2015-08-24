package de.gzockoll.prototype.assets.camel;

import com.hazelcast.util.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MD5Helper {
    public byte[] calculateS3Hash(File file) throws IOException {
        return Base64.encode(DigestUtils.md5(new FileInputStream(file)));
    }
}
