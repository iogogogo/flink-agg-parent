package com.iogogogo;

import com.iogogogo.context.FlinkKafkaPartitionType;
import com.iogogogo.util.Java8DateTimeUtils;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tao.zeng on 2020/6/4.
 */
public class DateTimeTests {

    @Test
    public void test() throws ParseException {

        String str = "2020-06-04T21:44:42.51";
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";

        DateFormat sdf = new SimpleDateFormat(pattern);
        Date date = sdf.parse(str);

        System.out.println(date);
        System.out.println(Java8DateTimeUtils.toJava8DateTime(date.toInstant()));

        System.out.println(Java8DateTimeUtils.parse(str, pattern));
        System.out.println(Java8DateTimeUtils.parseJava8(str, pattern));
    }

    @Test
    public void test1() {
        System.out.println(FlinkKafkaPartitionType.ROUND_ROBIN.name());
    }
}
