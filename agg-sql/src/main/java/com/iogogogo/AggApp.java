package com.iogogogo;

import com.iogogogo.agg.AggSql;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by tao.zeng on 2020/6/3.
 */
@Slf4j
public class AggApp {

    public static void main(String[] args) throws Exception {
        new AggSql().agg(args);
    }
}
