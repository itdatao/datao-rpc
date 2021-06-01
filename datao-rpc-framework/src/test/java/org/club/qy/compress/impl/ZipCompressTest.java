package org.club.qy.compress.impl;

import com.google.errorprone.annotations.Var;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author hht
 * @Date 2021/5/18 21:30
 */
class ZipCompressTest {

    byte[] data = "1243dsafdhfasjdghawyeoqcnlzdnfdfvseiruwpoewetgsdhakcholewjrpw".getBytes();
    ZipCompress zipCompress= new ZipCompress();


    @Test
    void decompress() {
        byte[] compress = zipCompress.compress(data);
        byte[] decompress = zipCompress.decompress(compress);
        System.out.println(decompress.length);
    }

    @Test
    void compress() {
        System.out.println(data.length);
        byte[] compress = zipCompress.compress(data);
        System.out.println(compress.length);
    }
}