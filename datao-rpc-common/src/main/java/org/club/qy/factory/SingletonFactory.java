package org.club.qy.factory;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author hht
 * @Date 2021/5/21 10:34
 */
@Slf4j
public final class SingletonFactory {

    //私有化构造函数
    private SingletonFactory(){}

    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    public static <T> T getInstance(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("singleton instance class type is null");
        }
        String key = clazz.toString();
        if (OBJECT_MAP.containsKey(key)) {
            return clazz.cast(OBJECT_MAP.get(key));
        } else {

                return clazz.cast(OBJECT_MAP.computeIfAbsent(key,k->{
                    try {
                        return clazz.getDeclaredConstructor().newInstance();
                    }catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }));
        }

    }
}
