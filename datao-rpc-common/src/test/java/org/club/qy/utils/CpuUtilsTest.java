package org.club.qy.utils;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author hht
 * @Date 2021/5/18 9:52
 */
class CpuUtilsTest {

    @Test
    void getCpu() throws UnsupportedEncodingException {

        System.out.println(CpuUtils.getCpu());
    }
}