package org.club.qy.extension;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @Author hht
 * @Date 2021/5/19 15:43
 */
@Slf4j
public final class ExtensionLoader<T> {
    private static final String SERVICE_DIRECTORY = "META-INF/ext";
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> EXTENSION_INSTANCE = new ConcurrentHashMap<>();
    private final Class<?> type;
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();
    private final Map<String, Holder<Object>> cachedInstance = new ConcurrentHashMap<>();


    public ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        //类型不能为null
        if (type == null) {
            throw new IllegalArgumentException("type must not null!");
        }
        //必须是接口类型
        if (!type.isInterface()) {
            throw new IllegalArgumentException("must be is a interface");
        }
        //必须有@SPI注解
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("must be a annotation by @SPI");
        }
        //先从map缓存中获取扩展类加载器
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        //没有就创建
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
            //extensionLoader = new ExtensionLoader<>(type); thread no safe
        }
        return extensionLoader;
    }

    public T getExtension(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }
        // firstly get from cache, if not hit, create one
        Holder<Object> holder = cachedInstance.get(name);
        if (holder == null) {
            cachedInstance.putIfAbsent(name, new Holder<>());
            holder = cachedInstance.get(name);
        }
        // create a singleton if no instance exists
        Object instance = holder.getValue();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.getValue();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.setValue(instance);
                }
            }
        }
        return (T) instance;

    }

    /**
     * 加载所有T类型的扩展类
     *
     * @param name 通过名称指定加载
     * @return
     */
    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new RuntimeException("no such extents class: " + name);
        }
        T instance = (T) EXTENSION_INSTANCE.get(clazz);

        if (instance == null) {
            try {
                EXTENSION_INSTANCE.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCE.get(clazz);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    private Map<String, Class<?>> getExtensionClasses() {

        Map<String, Class<?>> classes = cachedClasses.getValue();
        //考虑线程安全
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.getValue();
                if (classes == null) {
                    classes = new HashMap<>();
                    //加载指定目录下所有的扩展类，然后放到Holder中
                    loadDirectory(classes);
                    cachedClasses.setValue(classes);
                }
            }
        }
        return classes;


    }

    /**
     * 加载默认目录下的所有扩展类
     * @param classes
     */
    private void loadDirectory(Map<String, Class<?>> classes) {
        String filename = ExtensionLoader.SERVICE_DIRECTORY+"/"+type.getName();
        Enumeration<URL> urls;
        ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
        try {
            urls = classLoader.getResources(filename);
            if (urls!=null){
                URL resourceUrl = urls.nextElement();
                loadResource(classes, classLoader, resourceUrl);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }


    }

    /**
     * 加载/META-INF/ext/xxx.txt 解析到classes中
     * @param classes
     * @param classLoader
     * @param resourceUrl
     */
    private void loadResource(Map<String, Class<?>> classes, ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8))) {
            String line;
            // read every line
            while ((line = reader.readLine()) != null) {
                // get index of comment
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    // string after # is comment so we ignore it
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    try {
                        final int ei = line.indexOf('=');
                        String name = line.substring(0, ei).trim();
                        String clazzName = line.substring(ei + 1).trim();
                        // our SPI use key-value pair so both of them must not be empty
                        if (name.length() > 0 && clazzName.length() > 0) {
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            classes.put(name, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }


}
