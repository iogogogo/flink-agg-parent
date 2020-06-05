package com.iogogogo.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Created by tao.zeng on 2020/6/3.
 */
@Slf4j
public class Java8DateTimeUtils {

    public static LocalDate now() {
        return LocalDate.now();
    }

    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now();
    }

    public static Instant toInstant(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        return localDateTime.atZone(zone).toInstant();
    }

    public static long toEpochMilli(LocalDateTime localDateTime) {
        return toInstant(Objects.nonNull(localDateTime) ? localDateTime : nowDateTime()).toEpochMilli();
    }

    public static long toEpochMilli() {
        return toInstant(nowDateTime()).toEpochMilli();
    }

    public static LocalDateTime toJava8DateTime(long timestamp) {
        return zonedDateTime(timestamp).toLocalDateTime();
    }

    public static LocalDate toJava8Date(long timestamp) {
        return zonedDateTime(timestamp).toLocalDate();
    }

    public static LocalDateTime toJava8DateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static LocalDate toJava8Date(Instant instant) {
        return toJava8DateTime(instant).toLocalDate();
    }

    public static ZonedDateTime zonedDateTime(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8));
    }


    public static LocalDateTime parse(String value, boolean isJava8, String pattern) {
        if (isJava8) {
            try {
                return StringUtils.isNotEmpty(pattern) ? LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern)) : LocalDateTime.parse(value);
            } catch (DateTimeParseException e) {
                log.warn("ignore", e);
            }
        } else {
            try {
                return toJava8DateTime(new SimpleDateFormat(pattern).parse(value).toInstant());
            } catch (ParseException e) {
                log.warn("parse ts failure", e);
            }
        }
        return null;
    }

    public static LocalDateTime parseJava8(String value) {
        return parse(value, true, null);
    }

    public static LocalDateTime parseJava8(String value, String pattern) {
        return parse(value, true, pattern);
    }

    public static LocalDateTime parse(String value, String pattern) {
        return parse(value, false, pattern);
    }
}
