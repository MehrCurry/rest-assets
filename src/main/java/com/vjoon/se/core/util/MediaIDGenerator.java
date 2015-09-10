package com.vjoon.se.core.util;

import com.vjoon.se.core.entity.NameSpace;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by gzockoll on 25/08/15.
 */
public class MediaIDGenerator {

    public static String generateID(NameSpace nameSpace, String key) {
        return DigestUtils.sha256Hex(nameSpace + "#" + key);
    }
}
