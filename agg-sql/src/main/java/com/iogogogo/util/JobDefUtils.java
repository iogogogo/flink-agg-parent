package com.iogogogo.util;

import com.iogogogo.AggApp;
import com.iogogogo.definition.JobDefinition;
import com.iogogogo.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static com.iogogogo.consts.BaseConsts.JOB_DEFINITION_KEY;

/**
 * Created by tao.zeng on 2020/6/3.
 */
@Slf4j
public class JobDefUtils {

    public static JobDefinition getJobDefinition(ParameterTool parameterTool) {
        try {
            URL url = AggApp.class.getClassLoader().getResource("agg.json");
            String confJson = null;
            if (Objects.nonNull(parameterTool) && parameterTool.has(JOB_DEFINITION_KEY)) {
                String jobConfFile = parameterTool.get(JOB_DEFINITION_KEY);
                confJson = new String(Files.readAllBytes(Paths.get(jobConfFile)), StandardCharsets.UTF_8);
            } else if (Objects.nonNull(url)) {
                try {
                    confJson = new String(Files.readAllBytes(Paths.get(url.getPath())), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.warn("JobDefinition NOT FOUND");
            }

            if (StringUtils.isEmpty(confJson)) {
                throw new BizException("JobDefinition NOT FOUND");
            }
            return JsonParse.parse(confJson, JobDefinition.class);
        } catch (IOException e) {
            log.error("JobDefinition parse failure.", e);
        }
        return null;
    }
}
