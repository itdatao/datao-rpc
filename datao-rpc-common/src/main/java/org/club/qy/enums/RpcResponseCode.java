package org.club.qy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author hht
 * @Date 2021/5/18 9:06
 */
@AllArgsConstructor
@Getter
public enum RpcResponseCode {

    SUCCESS(200,"call successfully"),
    FAIL(500,"call failure!")
    ;
    private final  int code;
    private final  String message;
}
