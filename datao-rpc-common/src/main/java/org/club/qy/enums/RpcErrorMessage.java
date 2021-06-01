package org.club.qy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author hht
 * @Date 2021/5/18 9:06
 */
@Getter
@AllArgsConstructor
public enum RpcErrorMessage {
    //客户端连接失败
    //服务端没有此服务
    //服务端执行错误
    //请求和响应结果不匹配
    //服务接口没有任何实现
    CLIENT_CONNECT_FAIL("客户端连接失败"),
    SERVER_NONE_SERVICE("服务端没有此服务"),
    SERVER_EXECUTE_FAIL("服务端执行错误"),
    SERVER_NOT_IMPLEMENTS("服务接口没有任何实现"),
    REQUEST_NOT_MATCH_RESPONSE("请求和响应结果不匹配")
    ;

    private final String errorMessage;

}
