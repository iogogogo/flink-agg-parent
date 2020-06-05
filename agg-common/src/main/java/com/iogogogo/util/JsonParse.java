package com.iogogogo.util;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tao.zeng on 2020/6/3.
 */
@Slf4j
public class JsonParse {

    public static Type MAP_STR_OBJ_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();

    public static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(MAP_STR_OBJ_TYPE, new MapDeserializerDoubleAsIntFix())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();


    /**
     * To json string.
     *
     * @param bean the bean
     * @return the string
     */
    public static String toJson(Object bean) {
        return GSON.toJson(bean);
    }

    /**
     * To json string.
     *
     * @param builder the builder
     * @param bean    the bean
     * @return the string
     */
    public static String toJson(GsonBuilder builder, Object bean) {
        return builder.create().toJson(bean);
    }

    /**
     * Parse t.
     *
     * @param <T>  the type parameter
     * @param json the json
     * @param clz  the clz
     * @return the t
     */
    public static <T> T parse(String json, Class<T> clz) {
        return GSON.fromJson(json, clz);
    }

    /**
     * Parse t.
     *
     * @param <T>     the type parameter
     * @param builder the builder
     * @param json    the json
     * @param clz     the clz
     * @return the t
     */
    public static <T> T parse(GsonBuilder builder, String json, Class<T> clz) {
        return builder.create().fromJson(json, clz);
    }

    /**
     * Parse t.
     *
     * @param <T>  the type parameter
     * @param json the json
     * @param type the type
     * @return the t
     */
    public static <T> T parse(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    /**
     * Parse t.
     *
     * @param <T>     the type parameter
     * @param builder the builder
     * @param json    the json
     * @param type    the type
     * @return the t
     */
    public static <T> T parse(GsonBuilder builder, String json, Type type) {
        return builder.create().fromJson(json, type);
    }

    /**
     * To json bytes byte [ ].
     *
     * @param value the value
     * @return the byte [ ]
     */
    public static byte[] toJsonBytes(Object value) {
        return toJson(value).getBytes(StandardCharsets.UTF_8);
    }


    /**
     * Created by tao.zeng on 2020/6/4.
     * <p>
     * 处理LocalDate的序列化与反序列化
     */
    public final static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        @Override
        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        @Override
        public LocalDate deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            String timestamp = element.getAsJsonPrimitive().getAsString();
            return LocalDate.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    /**
     * https://gist.github.com/xingstarx/5ddc14ff6ca68ba4097815c90d1c47cc
     * <p>
     * https://stackoverflow.com/questions/36508323/how-can-i-prevent-gson-from-converting-integers-to-doubles/36529534#36529534
     * <p>
     * Created by tao.zeng on 2020/6/4.
     * <p>
     * 处理LocalDateTime序列化与反序列化
     */
    public final static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        @Override
        public LocalDateTime deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            String timestamp = element.getAsJsonPrimitive().getAsString();
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    /**
     * Created by tao.zeng on 2020/6/4.
     * <p>
     * 解决json数据转换为map结构的时候，会出现int变成double的问题
     */
    public final static class MapDeserializerDoubleAsIntFix implements JsonDeserializer<Map<String, Object>> {

        @SuppressWarnings("unchecked")
        @Override
        public Map<String, Object> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            return (Map<String, Object>) read(element);
        }

        private Object read(JsonElement in) {
            if (in.isJsonArray()) {
                List<Object> list = new ArrayList<>();
                JsonArray arr = in.getAsJsonArray();
                for (JsonElement anArr : arr) {
                    list.add(read(anArr));
                }
                return list;
            } else if (in.isJsonObject()) {
                Map<String, Object> map = new LinkedTreeMap<>();
                JsonObject obj = in.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
                for (Map.Entry<String, JsonElement> entry : entitySet) {
                    map.put(entry.getKey(), read(entry.getValue()));
                }
                return map;
            } else if (in.isJsonPrimitive()) {
                JsonPrimitive prim = in.getAsJsonPrimitive();
                if (prim.isBoolean()) {
                    return prim.getAsBoolean();
                } else if (prim.isString()) {
                    return prim.getAsString();
                } else if (prim.isNumber()) {
                    Number num = prim.getAsNumber();
                    // here you can handle double int/long values
                    // and return any type you want
                    // this solution will transform 3.0 float to long values
                    if (Math.ceil(num.doubleValue()) == num.longValue())
                        return num.longValue();
                    else {
                        return num.doubleValue();
                    }
                }
            }
            return null;
        }
    }
}
