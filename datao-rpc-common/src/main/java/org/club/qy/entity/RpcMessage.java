package org.club.qy.entity;

import lombok.*;

/**
 * 发送请求消息
 * @Author hht
 * @Date 2021/5/18 17:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class RpcMessage {
    //压缩类型
    private byte compressType;
    //序列化类型
    private byte codec;
    //消息类型
    private byte messageType;
    //请求id
    private int requestId;
    //消息体
    private Object data;

}
