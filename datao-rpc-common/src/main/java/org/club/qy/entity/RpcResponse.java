package org.club.qy.entity;

import lombok.*;
import org.club.qy.enums.RpcResponseCode;

/**
 * @Author hht
 * @Date 2021/5/18 17:04
 */
@Data
@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> {
    //请求ID
    private String requestId;
    //响应结果
    private T data;
    //响应消息
    private String message;
    //响应状态码 200调用成功 500调用失败
    private Integer code;

    public static <T>RpcResponse success(T data,String requestId){
        return RpcResponse.builder().code(RpcResponseCode.SUCCESS.getCode())
                .message(RpcResponseCode.SUCCESS.getMessage())
                .requestId(requestId)
                .data(data).build();
    }

    public static RpcResponse failure(RpcResponseCode response){
        return RpcResponse.builder().code(response.FAIL.getCode())
                .message(response.FAIL.getMessage())
              .build();
    }

}
