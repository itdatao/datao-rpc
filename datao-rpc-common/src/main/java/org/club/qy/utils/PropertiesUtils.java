package org.club.qy.utils;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


/**
 * @Author hht
 * @Date 2021/5/18 9:38
 */
@Slf4j
public class PropertiesUtils {
    private PropertiesUtils(){

    }

    /**
     *
     * @param fileName 文件路径
     * @return properties对象
     */
    public static Properties getProperties(String fileName){


        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String rpcConfigPath = "";
        if (url != null) {
            rpcConfigPath = url.getPath() + fileName;
        }
        Properties properties = null;
        try (InputStreamReader inputStreamReader = new InputStreamReader(
                new FileInputStream(rpcConfigPath), StandardCharsets.UTF_8)) {
            properties = new Properties();
            properties.load(inputStreamReader);
        } catch (IOException e) {
            log.error("occur exception when read properties file [{}]", fileName);
        }
        return properties;

    }

}
