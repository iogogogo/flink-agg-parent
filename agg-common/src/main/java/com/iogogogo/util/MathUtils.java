package com.iogogogo.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by tao.zeng on 2020/6/4.
 */
@Slf4j
public class MathUtils {

    public static BigDecimal bigDecimal(String val) {
        try {
            return new BigDecimal(val);
        } catch (Exception e) {
            // ignore
            log.warn("parse bigDecimal String failure", e);
        }
        return null;
    }

    public static BigDecimal bigDecimal(Object val) {
        try {
            return bigDecimal(val.toString());
        } catch (Exception e) {
            // ignore
            log.warn("parse bigDecimal Object failure", e);
        }
        return null;
    }


    public static Float tryFloat(Object val) {
        if (Objects.isNull(val)) return null;
        return tryFloat(val.toString());
    }

    public static Float tryFloat(String val) {
        if (StringUtils.isEmpty(val)) return null;
        BigDecimal decimal = bigDecimal(val);
        return decimal != null ? decimal.floatValue() : null;
    }

    public static Double tryDouble(Object val) {
        if (Objects.isNull(val)) return null;
        return tryDouble(val.toString());
    }

    public static Double tryDouble(String val) {
        if (StringUtils.isEmpty(val)) return null;
        BigDecimal decimal = bigDecimal(val);
        return decimal != null ? decimal.doubleValue() : null;
    }

    public static Long tryLong(Object val) {
        if (Objects.isNull(val)) return null;
        return tryLong(val.toString());
    }

    public static Long tryLong(String val) {
        if (StringUtils.isEmpty(val)) return null;
        BigDecimal decimal = bigDecimal(val);
        return decimal != null ? decimal.longValue() : null;
    }
}
