package de.gzockoll.prototype.assets.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by gzockoll on 25/08/15.
 */
public class MediaIDGenerator {
    public static String generateID(String nameSpace,String key) {
        return DigestUtils.sha256Hex(nameSpace + "#" + key);
    }
}
