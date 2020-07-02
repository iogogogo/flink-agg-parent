package com.iogogogo.util;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tao.zeng on 2020/6/11.
 */

public class TransferUtils {

    private static final String ENCODING = "utf-8";

    public static void yml2Properties(String path) {
        final String DOT = ".";
        List<String> lines = new LinkedList<>();
        YAMLFactory yamlFactory = new YAMLFactory();
        try (YAMLParser parser = yamlFactory.createParser(
                new InputStreamReader(new FileInputStream(path), Charset.forName(ENCODING)));
        ) {

            StringBuilder key = new StringBuilder();
            String value;
            JsonToken token = parser.nextToken();
            while (token != null) {
                if (JsonToken.START_OBJECT.equals(token)) {
                    // do nothing
                } else if (JsonToken.FIELD_NAME.equals(token)) {
                    if (key.length() > 0) {
                        key.append(DOT);
                    }
                    key.append(parser.getCurrentName());

                    token = parser.nextToken();
                    if (JsonToken.START_OBJECT.equals(token)) {
                        continue;
                    }
                    value = parser.getText();
                    lines.add(key + "=" + value);

                    int dotOffset = key.lastIndexOf(DOT);
                    if (dotOffset > 0) {
                        key = new StringBuilder(key.substring(0, dotOffset));
                    }
                } else if (JsonToken.END_OBJECT.equals(token)) {
                    int dotOffset = key.lastIndexOf(DOT);
                    if (dotOffset > 0) {
                        key = new StringBuilder(key.substring(0, dotOffset));
                    } else {
                        key = new StringBuilder();
                        lines.add("");
                    }
                }
                token = parser.nextToken();
            }
            System.out.println(lines);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void properties2Yaml(String path) {

        JavaPropsFactory factory = new JavaPropsFactory();
        YAMLFactory yamlFactory = new YAMLFactory();
        try (JsonParser parser = factory.createParser(
                new InputStreamReader(new FileInputStream(path), Charset.forName(ENCODING)));

             YAMLGenerator generator = yamlFactory.createGenerator(
                     new OutputStreamWriter(new FileOutputStream(path), Charset.forName(ENCODING)));) {

            JsonToken token = parser.nextToken();

            while (token != null) {
                if (JsonToken.START_OBJECT.equals(token)) {
                    generator.writeStartObject();
                } else if (JsonToken.FIELD_NAME.equals(token)) {
                    generator.writeFieldName(parser.getCurrentName());
                } else if (JsonToken.VALUE_STRING.equals(token)) {
                    generator.writeString(parser.getText());
                } else if (JsonToken.END_OBJECT.equals(token)) {
                    generator.writeEndObject();
                }
                token = parser.nextToken();
            }
            generator.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
