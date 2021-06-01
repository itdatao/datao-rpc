package org.club.qy.compress;

import org.club.qy.extension.SPI;

/**
 * @Author hht
 * @Date 2021/5/18 17:01
 */
@SPI
public interface Compress {
    //解压
    byte[] decompress(byte[] data);
    //压缩
    byte[] compress(byte[] data);
}
