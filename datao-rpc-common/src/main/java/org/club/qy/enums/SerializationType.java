package org.club.qy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author hht
 * @Date 2021/5/18 9:07
 */
@AllArgsConstructor
@Getter
public enum SerializationType {
    KYRO((byte)1,"kryo"),
    PROTOSTUFF((byte)2,"protostuff"),
    HESSIAN2((byte)3,"hessian2")
    ;
    private final byte type;
    private final String name;

    public static String getSerializedName(int type){
        for (SerializationType value : SerializationType.values()) {
            if (value.type==type){
                return value.name;
            }
        }

        return null;
    }


}
