package org.club.qy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author hht
 * @Date 2021/5/18 9:53
 */
@AllArgsConstructor
@Getter
public enum CompressType {
    //压缩类型
    GZIP((byte)1, "gzip"),
    ZIP((byte)2, "zip");
    private final byte code;
    private final String name;

    public static String getName(int code) {
        for (CompressType c : CompressType.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}

