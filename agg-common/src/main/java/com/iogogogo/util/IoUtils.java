package com.iogogogo.util;

/**
 * Created by tao.zeng on 2020/6/11.
 */
public class IoUtils {

    public static void close(AutoCloseable... cloneables) {
        if (cloneables == null || cloneables.length == 0) return;
        try {
            for (AutoCloseable io : cloneables) io.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
