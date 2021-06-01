package org.club.qy.serialize;

import org.club.qy.extension.SPI;

/**
 * @Author hht
 * @Date 2021/5/18 21:40
 */
@SPI
public interface Serialization {
    //序列化
    byte[] serialize(Object obj);

    //反序列化
    <T> T deserialize(byte[] data,Class<T> clazz);
}
