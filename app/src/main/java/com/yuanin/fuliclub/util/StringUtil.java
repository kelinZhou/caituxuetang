package com.yuanin.fuliclub.util;

import java.util.UUID;

/**
 * @author：tqzhang on 18/8/15 11:29
 */
public class StringUtil {

    public static String getEventKey() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }
}
