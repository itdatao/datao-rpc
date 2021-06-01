package org.club.qy.utils;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author hht
 * @Date 2021/5/18 9:45
 */
class PropertiesUtilsTest {


    @Test
    void getProperties() {
        Properties properties = PropertiesUtils.getProperties("./abc.properties");
        System.out.println(properties.getProperty("abc"));
    }
}