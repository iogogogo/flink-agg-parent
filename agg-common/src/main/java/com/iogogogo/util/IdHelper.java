package com.iogogogo.util;

import java.util.UUID;

/**
 * Created by tao.zeng on 2019-12-16.
 */
public class IdHelper {

    public static String id() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
