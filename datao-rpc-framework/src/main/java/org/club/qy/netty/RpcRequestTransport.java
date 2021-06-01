package org.club.qy.netty;

import org.club.qy.entity.RpcRequest;
import org.club.qy.extension.SPI;

/**
 * @Author hht
 * @Date 2021/5/20 17:01
 */
@SPI
public interface RpcRequestTransport {

    Object sendRpcRequest(RpcRequest rpcRequest);

}
