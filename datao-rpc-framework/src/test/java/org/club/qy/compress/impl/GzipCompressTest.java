package org.club.qy.compress.impl;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author hht
 * @Date 2021/5/18 21:35
 */
class GzipCompressTest {

    byte[] data = "1243dsafdhfasjdghawyeoqcnlzdnfdfvseiruwpoewetgsdhakcholewjrpw".getBytes();
    GzipCompress gzipCompress = new GzipCompress();
    byte[] compress = null;
    @Test
    void decompress() {
        compress = gzipCompress.compress(data);
        byte[] decompress = gzipCompress.decompress(compress);
        System.out.println("解压后的长度："+decompress.length);
    }

    @Test
    void compress() {
        System.out.println("压缩前的长度："+data.length);
        compress = gzipCompress.compress(data);
        System.out.println("压缩后的长度："+compress.length);
    }
}